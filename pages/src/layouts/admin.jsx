import React from 'react';
import {Divider, Layout, Menu, Space} from 'antd';
import {history, Link} from 'umi';
import "./layout.css"

const {Header, Content, Sider} = Layout;


export default class extends React.Component {

  state = {
    userInfo: {}
  };


  logout = () => {
    localStorage.clear();
    history.push("/login")
  };

  account = () => {
    history.push("/admin/userCenter/account")
  };

  render() {
    return (
      <Layout>
        <Sider width={150}>
          <Space direction="vertical" style={{width: '100%', margin: 20}}>
            <Divider style={{height: 1}}></Divider>

            <Link to="/admin">首页</Link>

            <h3>交付中心</h3>
            <Link to="/admin/repository">镜像仓库</Link>
            <a>我的收藏</a>


            <h3>应用平台</h3>
            <Link to="/admin/host">主机</Link>
            <Link to="/admin/app">应用</Link>
            <Link to="/admin/project">项目</Link>


            <h3>设置</h3>
            <Link to="/admin/user/registry">仓库配置</Link>
            <Link to="/admin/user/runner">执行器</Link>
          </Space>

        </Sider>

        <Layout>
          <Header className="admin-header">
            <div className="right-btn">
              <a onClick={this.account}>修改密码</a>
              <a onClick={this.logout}>退出登录</a>
            </div>
          </Header>

          <Content style={{minHeight: '100vh'}}>
            {this.props.children}
          </Content>
        </Layout>
      </Layout>
    );

  }

}


