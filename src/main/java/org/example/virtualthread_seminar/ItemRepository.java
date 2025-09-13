package org.example.virtualthread_seminar;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ItemRepository {
    private final JdbcTemplate jdbc;

    public ItemRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Map<String, Object> findById(long id) {
        try {
            return jdbc.queryForMap(
                    "SELECT id, name, price FROM item WHERE id = ?", id
            );
        } catch (EmptyResultDataAccessException e) {
            // id가 없으면 404로 응답
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found: " + id);
        }
    }
}
