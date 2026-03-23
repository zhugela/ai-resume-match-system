package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liangge.resumematch.entity.Question;
import com.liangge.resumematch.mapper.QuestionMapper;
import com.liangge.resumematch.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    @Override
    public List<Map<String, Object>> search(String keyword, String category, String difficulty) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Question::getQuestion, keyword)
                   .or()
                   .like(Question::getAnswer, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Question::getCategory, category);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        
        wrapper.orderByDesc(Question::getViews);
        
        return questionMapper.selectList(wrapper).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Question q = questionMapper.selectById(id);
        if (q != null) {
            // 增加浏览次数
            q.setViews(q.getViews() + 1);
            questionMapper.updateById(q);
        }
        return q != null ? toMap(q) : null;
    }

    private Map<String, Object> toMap(Question q) {
        return Map.of(
            "id", q.getId(),
            "category", q.getCategory() != null ? q.getCategory() : "",
            "difficulty", q.getDifficulty() != null ? q.getDifficulty() : "MEDIUM",
            "question", q.getQuestion() != null ? q.getQuestion() : "",
            "answer", q.getAnswer() != null ? q.getAnswer() : "",
            "tags", q.getTags() != null ? q.getTags() : "[]",
            "views", q.getViews() != null ? q.getViews() : 0
        );
    }
}
