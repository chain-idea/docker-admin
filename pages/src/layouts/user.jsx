import React from 'react';
import {Layout, Menu} from 'antd';
import {history} from 'umi';
import "./layout.css"
import "./user.css"

const {Header, Content, Footer} = Layout;


// 用户布局
export default class extends React.Component {

  state = {
    topMenu: [],
    slideMenu: [],
    currentTop: {},
    currentSlide: {},
    pathname: null
  };

  componentDidMount() {
    this.state.pathname = history.location.pathname;
    this.setState(this.state)
  }

  onSelect = (event) => {
    let key = event.key;
    history.push(key)
  };


  render() {
    return (
      <Layout>
        <Header style={{position: 'fixed', zIndex: 1, width: '100%'}} className="admin-header">
          <div className="logo">
           DockerAdmin
          </div>



        </Header>
        <Content className="site-layout" style={{paddingTop: 24, paddingLeft: 50, paddingRight: 50, marginTop: 64}}>
          <div className="site-layout-background" style={{padding: 24, minHeight: 'calc( 100vh - 88px)', background: this.state.pathname == '/login' ? 'none': null}}>
            {this.props.children}
          </div>
        </Content>
      </Layout>
    );

  }

}

