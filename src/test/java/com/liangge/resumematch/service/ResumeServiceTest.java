package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.mapper.ResumeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ResumeService 单元测试 - JUnit5 + Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("简历服务测试")
class ResumeServiceTest {

    @Mock
    private ResumeMapper resumeMapper;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private Resume testResume;

    @BeforeEach
    void setUp() {
        testResume = new Resume();
        testResume.setId(1L);
        testResume.setUserId(100L);
        testResume.setFileName("test_resume.pdf");
        testResume.setFileUrl("/uploads/test_resume.pdf");
        testResume.setFileType("PDF");
        testResume.setFileSize(1024L);
        testResume.setAnalysisStatus("PENDING");
        testResume.setCreatedAt(LocalDateTime.now());
        testResume.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("根据ID获取简历 - 成功")
    void getById_Success() {
        // Given
        when(resumeMapper.selectById(1L)).thenReturn(testResume);

        // When
        Resume result = resumeService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test_resume.pdf", result.getFileName());
        verify(resumeMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("根据ID获取简历 - 不存在")
    void getById_NotFound() {
        // Given
        when(resumeMapper.selectById(999L)).thenReturn(null);

        // When
        Resume result = resumeService.getById(999L);

        // Then
        assertNull(result);
        verify(resumeMapper, times(1)).selectById(999L);
    }

    @Test
    @DisplayName("获取用户简历列表")
    void listByUserId() {
        // Given
        Resume resume2 = new Resume();
        resume2.setId(2L);
        resume2.setUserId(100L);
        resume2.setFileName("resume2.pdf");

        List<Resume> resumes = Arrays.asList(testResume, resume2);
        when(resumeMapper.selectList(any())).thenReturn(resumes);

        // When
        List<Resume> result = resumeService.list();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("保存简历 - 成功")
    void saveResume_Success() {
        // Given
        when(resumeMapper.insert(any(Resume.class))).thenReturn(1);

        // When
        boolean result = resumeService.save(testResume);

        // Then
        assertTrue(result);
        verify(resumeMapper, times(1)).insert(any(Resume.class));
    }

    @Test
    @DisplayName("更新简历状态")
    void updateStatus() {
        // Given
        when(resumeMapper.updateById(any(Resume.class))).thenReturn(1);

        // When
        testResume.setAnalysisStatus("COMPLETED");
        boolean result = resumeService.updateById(testResume);

        // Then
        assertTrue(result);
        verify(resumeMapper, times(1)).updateById(any(Resume.class));
    }

    @Test
    @DisplayName("删除简历 - 成功")
    void deleteById_Success() {
        // Given
        when(resumeMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = resumeService.removeById(1L);

        // Then
        assertTrue(result);
        verify(resumeMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("分页查询简历")
    void pageQuery() {
        // Given
        Page<Resume> page = new Page<>(1, 10);
        List<Resume> records = Arrays.asList(testResume);
        page.setRecords(records);
        page.setTotal(1);

        when(resumeMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // When
        Page<Resume> result = resumeService.page(page);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("统计用户简历数量")
    void countByUserId() {
        // Given
        when(resumeMapper.selectCount(any())).thenReturn(5L);

        // When
        long count = resumeService.count();

        // Then
        assertEquals(5, count);
    }

    @Test
    @DisplayName("检查简历是否存在 - 存在")
    void existsById_True() {
        // Given
        when(resumeMapper.exists(any())).thenReturn(true);

        // When
        boolean exists = resumeService.exists(any());

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("检查简历是否存在 - 不存在")
    void existsById_False() {
        // Given
        when(resumeMapper.exists(any())).thenReturn(false);

        // When
        boolean exists = resumeService.exists(any());

        // Then
        assertFalse(exists);
    }
}
