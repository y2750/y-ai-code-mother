package com.yy.yaicodemother.aop;

import com.yy.yaicodemother.annotation.AuthCheck;
import com.yy.yaicodemother.exception.BusinessException;
import com.yy.yaicodemother.exception.ErrorCode;
import com.yy.yaicodemother.model.entity.User;
import com.yy.yaicodemother.model.enums.UserRoleEnum;
import com.yy.yaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ClassName: AuthInterceptor
 * Package: com.yy.yaicodemother.aop
 * Description: 权限拦截器，用于处理需要权限验证的请求
 *
 * @Author
 * @Create 2025/9/23 19:42
 * @Version 1.0
 */
@Aspect  // 声明这是一个切面类
@Component  // 声明这是一个Spring组件
public class AuthInterceptor {

    @Resource  // 自动注入UserService
    private UserService userService;



    /**
     * 环绕通知，用于拦截带有@AuthCheck注解的方法
     * @param joinPoint 连接点，可以获取方法执行的相关信息
     * @param authCheck 注解对象，可以获取注解中的属性值
     * @return 返回方法执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(authCheck)")  // 拦截带有@AuthCheck注解的方法
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取必须的角色
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);  // 获取当前登录用户信息
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if (mustRoleEnum == null) {  // 如果没有指定必须的角色，直接放行
            return joinPoint.proceed();
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());

        if (userRoleEnum == null) {  // 如果用户角色为空，抛出未登录异常
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 如果需要管理员权限，但用户不是管理员，抛出无权限异常
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 继续执行原始方法
        return joinPoint.proceed();
    }
}
