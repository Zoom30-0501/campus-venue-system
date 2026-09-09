package com.club.venue.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result 统一响应类单元测试
 */
@DisplayName("Result 统一响应测试")
class ResultTest {

    @Test
    @DisplayName("ok() - 无数据返回成功响应")
    void okWithoutData() {
        Result<Void> result = Result.ok();

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("ok(T) - 带数据返回成功响应")
    void okWithData() {
        String data = "测试数据";
        Result<String> result = Result.ok(data);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("测试数据", result.getData());
    }

    @Test
    @DisplayName("ok() - null数据")
    void okWithNullData() {
        Result<String> result = Result.ok(null);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("ok() - 复杂对象数据")
    void okWithComplexObject() {
        TestData data = new TestData(1L, "张三");
        Result<TestData> result = Result.ok(data);

        assertEquals(200, result.getCode());
        assertEquals(1L, result.getData().getId());
        assertEquals("张三", result.getData().getName());
    }

    @Test
    @DisplayName("error(String) - 默认500错误")
    void errorWithMessage() {
        Result<Void> result = Result.error("参数错误");

        assertEquals(500, result.getCode());
        assertEquals("参数错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("error(int, String) - 自定义错误码")
    void errorWithCodeAndMessage() {
        Result<Void> result = Result.error(400, "请求参数不正确");

        assertEquals(400, result.getCode());
        assertEquals("请求参数不正确", result.getMessage());
        assertNull(result.getData());
    }

    @ParameterizedTest
    @DisplayName("error - 不同HTTP状态码")
    @ValueSource(ints = {400, 401, 403, 404, 500, 502, 503})
    void errorWithVariousCodes(int code) {
        Result<Void> result = Result.error(code, "错误: " + code);

        assertEquals(code, result.getCode());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Result - setter/getter 正确性")
    void settersAndGetters() {
        Result<String> result = new Result<>();
        result.setCode(201);
        result.setMessage("created");
        result.setData("新创建的资源");

        assertEquals(201, result.getCode());
        assertEquals("created", result.getMessage());
        assertEquals("新创建的资源", result.getData());
    }

    // 测试用内部类
    @lombok.Data
    @lombok.AllArgsConstructor
    static class TestData {
        private Long id;
        private String name;
    }
}
