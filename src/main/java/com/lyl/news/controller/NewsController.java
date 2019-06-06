package com.lyl.news.controller;

import com.lyl.news.dao.NewsDAO;
import com.lyl.news.models.*;
import com.lyl.news.service.CommentService;
import com.lyl.news.service.NewsService;
import com.lyl.news.service.QiniuService;
import com.lyl.news.service.UserService;
import com.lyl.news.util.NewsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Controller
public class NewsController {

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NewsService newsService;

    //这个方法是通过文件名获取本地图片
    @RequestMapping(path = "/image", method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                           HttpServletResponse response){
        try {
            response.setContentType("image/png");
            StreamUtils.copy(new FileInputStream(new File(NewsUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //上传图片
    @RequestMapping(path = "/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){
        try {
            //调用qiniuService后返回的是上传成功的图片的url
            String fileUrl = qiniuService.saveImage(file);
            if(fileUrl != null){
                return NewsUtil.getJSONString(0, fileUrl);
            }
            return NewsUtil.getJSONString(1, "上传失败");
        }catch (Exception e){
            return NewsUtil.getJSONString(1, "上传失败");
        }
    }

    //添加新闻资讯
    @RequestMapping(value = "/user/addNews/", method = RequestMethod.POST)
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            News news = new News();
            news.setTitle(title);
            news.setImage(image);
            news.setLink(link);
            news.setCreatedDate(new Date());
            //hostHolder存储的用户状态，如果没有用户，则设置一个匿名用户
            if (hostHolder.getUser() == null) {
                news.setUserId(3);
            } else {
                news.setUserId(hostHolder.getUser().getId());
            }
            newsDAO.addNews(news);
            //添加成功后返回主页
            return "redirect:/";
        }catch (Exception e){
            System.out.println(e.getMessage());
            return NewsUtil.getJSONString(1, "发布失败");
        }
    }

    //请求新闻详情页
    //在写这个Controller的时候，其实主要是要去分析一下详情页有哪些需要显示的数据
    @RequestMapping(value = "/news/{newsId}", method = RequestMethod.GET)
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        try {
            News news = newsService.getById(newsId);
            if (news != null) {
                //获取新闻详情页对应的所有评论
                List<Comment> comments = commentService.getCommentsByEntity(newsId, EntityType.ENTITY_NEWS);
                //将评论的信息注入模版
                List<ViewObject> commentVOs = new ArrayList<>();
                for (Comment comment : comments) {
                    ViewObject commentVO = new ViewObject();
                    //注入评论信息
                    commentVO.set("comment", comment);
                    //注入评论人信息
                    commentVO.set("user", userService.getUser(comment.getUserId()));
                    commentVOs.add(commentVO);
                }
                model.addAttribute("comments", commentVOs);
            }
            model.addAttribute("news", news);
            //注入新闻资讯发布人的相关信息
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "detail";
    }

    //添加一条评论
    @RequestMapping(value = "/addComment", method = {RequestMethod.GET, RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setStatus(0);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setContent(content);
            comment.setUserId(hostHolder.getUser().getId());
            commentService.addComment(comment);
            //更新评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(newsId, count);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "redirect:/news/" + newsId;
    }

}
