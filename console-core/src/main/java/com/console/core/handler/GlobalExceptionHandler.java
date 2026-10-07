package com.console.core.handler;

import com.console.core.entity.ErrorRecord;
import com.console.core.service.ErrorRecordService;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.Result;
import com.console.framework.domain.ResultCode;
import com.console.framework.utils.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 业务包名前缀，用于定位异常发生的业务代码位置
    private static final String BASE_PACKAGE = "com.console";
    @Resource
    private ErrorRecordService errorRecordService;
    //------------兜底异常处理------------------
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception ex) {
        log.error("异常报错: ", ex);
        saveErrorRecord(ex);
        return Result.error(ex);
    }
    //------------服务异常处理-------------------
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("参数校验失败: ", ex);
        saveErrorRecord(ex);
        return Result.failed(ResultCode.PARAMS_ERROR.getCode(), "参数校验失败，请检查确认" + ex.getMessage());
    }
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("非法参数: ", ex);
        saveErrorRecord(ex);
        return Result.failed(ResultCode.PARAMS_ERROR.getCode(), "存在非法参数，请检查确认" + ex.getMessage());
    }
    //------------自定义抛出异常处理--------------
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<Result> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("业务异常: {}", ex.getMessage());
        Result errorResult = Result.error(ex);
        if (ex.getCode() == ResultCode.AUTHENTICATION_FAILED) {//401 未登录
            if (request != null) {
                String token = request.getHeader("Authorization");
                if (StringUtils.hasText(token)) {//有鞋带token，但为获取到token缓存信息
                    log.info("查询用户信息未失败，当前用户未登录，当前请求的token信息为：{}", token);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResult);
        } else if (ex.getCode() == ResultCode.UNAUTHORIZED_FAILED) {//403 无权限
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResult);
        } else if (ex.getCode() == ResultCode.NOT_FOUND) {//404 无此资源
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
        }
        saveErrorRecord(ex);
        return ResponseEntity.status(HttpStatus.OK).body(errorResult);
    }

    private void saveErrorRecord(Exception ex) {
        // 提取异常位置
        StackTraceElement[] stackTrace = ex.getStackTrace();
        String locationClass = "Unknown";
        String locationMethod = "Unknown";
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith(BASE_PACKAGE)) {
                locationClass = element.getClassName();
                locationMethod = element.getMethodName();
                break;
            }
        }
        // 原因链
        String causeChain = buildCauseChain(ex);
        // 堆栈（开发环境或完全存储根据需求，此处生产也存）
        String stackTraceStr = getStackTrace(ex);
        log.error(
                "\n=========================================前端API接口异常信息===========================================" +
                "\n----【异常时间】：{}" +
                "\n----【异常入口和函数】：{}----{}" +
                "\n----【异常触发原因】：{}" +
                "\n----【异常堆栈信息】：{}", LocalDateTime.now(),locationClass,locationMethod,causeChain,stackTraceStr);
        ErrorRecord errorRecord = ErrorRecord.builder()
                .errorClass(locationClass)
                .errorMethod(locationMethod)
                .stackTrace(stackTraceStr)
                .errorDesc(causeChain)
                .errorTime(DateUtils.getCurrentTimestamp())
                .build();
        errorRecordService.saveErrorRecord(errorRecord);
    }

    private String buildCauseChain(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        Throwable current = ex;
        while (current != null) {
            sb.append(current.getClass().getSimpleName());
            if (current.getMessage() != null) {
                sb.append(": ").append(current.getMessage());
            }
            current = current.getCause();
            if (current != null) {
                sb.append(" → ");
            }
        }
        return sb.toString();
    }

    private String getStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }

    private String getCurrentUserId() {
        // 若集成 Spring Security 可使用 SecurityContextHolder
        return "anonymous";
    }
//    @ExceptionHandler(Exception.class)
//    public Object handleException(Exception e) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        StackTraceElement[] stackTraceElements = e.getStackTrace();
//        if (stackTraceElements != null && stackTraceElements.length > 0) {
//            StackTraceElement element = stackTraceElements[0]; // 获取异常发生的第一个位置
//            String className          = element.getClassName();
//            String methodName         = element.getMethodName();
//            String fileName           = element.getFileName();
//            int lineNumber            = element.getLineNumber();
//            LocalDateTime timestamp   = LocalDateTime.now();
//            String message            = e.getMessage();
//            log.error(
//                    "\n=========================================前端API接口异常信息===========================================" +
//                    "\n----【异常时间】：" + timestamp.format(formatter) +
//                    "\n----【异常类型】：" + e.getClass().getSimpleName() +
//                    "\n----【异常入口类和函数】：" + className + "——" + methodName +
//                    "\n----【异常触发文件和位置】：" + fileName + "——" + lineNumber +
//                    "\n----【异常信息描述】：" + message +
//                    "\n=========================================异常信息===========================================\n", e
//            );
//        } else {
//            log.error("\n=========================================前端API接口异常信息===========================================" +
//                    "\n----【异常时间】：" + LocalDateTime.now().format(formatter) +
//                    "\n----【异常类型】：" + e.getClass().getSimpleName() +
//                    "\n----【异常信息描述】：" + e.getMessage() +
//                    "\n=========================================异常信息===========================================\n", e);
//        }
//        if (e instanceof CustomException customException) { //自定义异常
//            if (customException.getCode() == 401) {
//                return ResponseEntity.status(401).<Object>body(Result.failed(401, e.getMessage()));
//            }
//            return Result.failed(customException.getCode(), e.getMessage());
//        }
//        return Result.failed(500, e.getMessage());
//    }
}
