import React from 'react';
import {Form, Input, Button, Checkbox} from 'antd';
import {UserOutlined, LockOutlined} from '@ant-design/icons';
import "./login.css"
import {history, Link} from 'umi';
import http from "../utils/request";
import UploadImage from "../components/UploadImage";
import auth from "../utils/auth";

export default () => {
  const onFinish = values => {
    console.log('Received values of form: ', values);

    http.post('/api/login/account', values).then(rs => {
      auth.setIsLogin('true')
      localStorage.setItem('jwt', rs.data.jwt)
      history.push('/admin')
    })

  };
  return (
    <div className="login-page">


      <Form
        name="normal_login"
        className="login-form"
        initialValues={{remember: true}}
        onFinish={onFinish}
      >
        <h1>用户登录</h1>
        <Form.Item
          name="username"
          rules={[{required: true, message: '请输入用户名!'}]}
        >
          <Input prefix={<UserOutlined className="site-form-item-icon"/>} placeholder="用户名"     autoComplete="off"/>
        </Form.Item>
        <Form.Item
          name="password"
          rules={[{required: true, message: '请输入密码!'}]}
        >
          <Input
            autoComplete="off"
            prefix={<LockOutlined className="site-form-item-icon"/>}
            type="password"
            placeholder="密码"
          />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" className="login-form-button">
            登录
          </Button>
        </Form.Item>
      </Form>

    </div>
  );
}
