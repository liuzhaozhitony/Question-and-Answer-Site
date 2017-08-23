package com.nowcoder.dao;

import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by LIU ZHAOZHI on 2017-8-3.
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId}, #{data}, #{created_date}, #{type})"})
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
