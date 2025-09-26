package com.yy.yaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yy.yaicodemother.exception.BusinessException;
import com.yy.yaicodemother.exception.ErrorCode;
import com.yy.yaicodemother.model.dto.user.UserQueryRequest;
import com.yy.yaicodemother.model.entity.User;
import com.yy.yaicodemother.mapper.UserMapper;
import com.yy.yaicodemother.model.enums.UserRoleEnum;
import com.yy.yaicodemother.model.vo.LoginUserVO;
import com.yy.yaicodemother.model.vo.UserVO;
import com.yy.yaicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yy.yaicodemother.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/y2750">程序员yy</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{


    @Override
    /**
     * 用户注册方法
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 注册成功返回用户ID，否则抛出异常
     */
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 检查参数是否为空
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 检查账号长度是否小于4位
        if(userAccount.length()< 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于4位");
        }
        // 检查密码长度是否小于8位
        if(userPassword.length() < 8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        }
        // 检查两次输入的密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不一致");
        }

        // 查询数据库中是否已存在该账号
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        // 如果账号已存在，抛出异常
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        // 对密码进行加密处理
        String encryptedPassword = getEncryptPassword(userPassword);
        // 创建新用户对象
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        user.setUserName("null");
        user.setUserRole(UserRoleEnum.USER.getValue());
        // 保存用户信息到数据库
        boolean saveResult = this.save(user);
        // 如果保存失败，抛出异常
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误，注册失败");
        }

        // 返回新注册用户的ID
        return user.getId();
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于4位");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        }
        // 根据账号查询用户信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", getEncryptPassword(userPassword));
        User user = this.mapper.selectOneByQuery(queryWrapper);
        // 如果用户不存在或密码错误，抛出异常
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        currentUser = this.getById(currentUser.getId());

        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(currentUser == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
 * 重写方法，用于获取加密后的密码
 * @param password 原始密码字符串
 * @return 返回经过MD5加密后的密码字符串
 */
    @Override
    public String getEncryptPassword(String password) {
    // 定义一个常量盐值(SALT)，用于增强密码加密的安全性
        final String SALT = "nyy";
    // 将盐值与原始密码拼接后，使用MD5算法进行加密，并以十六进制字符串形式返回
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {

        if(userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        return QueryWrapper.create().eq("id",id).like("userName",userName).like("userAccount",userAccount).like("userProfile",userProfile).eq("userRole",userRole);
    }


}
