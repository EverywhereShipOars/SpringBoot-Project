package com.lyl.news.dao;

import com.lyl.news.models.News;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewsDAO {

    String TABLE_NAME = " news ";
    String INSERT_FIELDS = " title, link, image, like_count, comment_count, created_date, user_id";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title}, #{link}, #{image}, #{likeCount}, #{commentCount}, #{createdDate}, #{userId})"})
    int addNews(News news);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id = #{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, "where id = #{id}"})
    News selectById(int id);

    @Update({"update ", TABLE_NAME, " set like_count = #{likeCount} where id = #{id}"})
    int updateLikeCount(@Param("id") int id, @Param("likeCount") int likeCount);

    //找出最新的一些新闻
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, "  order by created_date desc limit #{offset}, #{limit}"})
    List<News> selectByUserIdAndOffset(@Param("offset") int offset,
                                     @Param("limit") int limit);
}
