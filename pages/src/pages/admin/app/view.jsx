import {
  Button,
  Card,
  Col,
  Form,
  Typography,
  Descriptions,
  Row,
  Space,
  Switch,
  Tabs,
  Divider,
  Alert,
  Modal,
  Tag, Input
} from 'antd';
import React from 'react';
import http from "@/utils/request";
import Log from "./Log";
import AppForm from "./AppForm";
import RemoteSelect from "../../../components/RemoteSelect";
import {history} from "umi";
import {LazyLog, ScrollFollow} from "react-lazylog";

let api = '/api/app/';


export default class extends React.Component {

  state = {
    app: {
      yaml: '',
      host: {}
    },
    container: {},

    moveApp: {
      targetHostId: null
    },
    publishApp: {
      targetVersion: null
    },

    showEditName: false,
    newName: ''

  }

  componentDidMount() {
    this.fetchData();
  }


  fetchData = () => {
    let {params} = this.props.match;
    http.get(api + "get", params).then(app => {
      this.setState({app,})

    })

    http.get("/api/app/container", params).then(container => {
      this.setState({container})
    })
  }

  deploy = () => {
    http.post('api/app/deploy/' + this.state.app.id).then(rs => {
    })
  }
  start = () => {
    http.post('api/app/start/' + this.state.app.id).then(rs => {
    })
  }
  stop = () => {
    http.post('api/app/stop/' + this.state.app.id).then(rs => {
    })
  }

  setAutoDeploy = (id, autoDeploy) => {
    http.get("/api/app/autoDeploy", {id, autoDeploy})
  }
  setAutoRestart = (id, autoRestart) => {
    http.get("/api/app/autoRestart", {id, autoRestart})
  }
  moveApp = () => {
    const id = this.state.app.id;
    const hostId = this.state.moveApp.targetHostId;
    http.get("/api/app/moveApp", {id, hostId}).then(rs => {
      this.fetchData()
    })
  }

  updateApp = () => {
    const id = this.state.app.id;
    const tag = this.state.publishApp.targetVersion;
    http.get("/api/app/updateApp", {id, tag}).then(rs => {
      this.fetchData()
    })
  }


  handleDelete = () => {
    const id = this.state.app.id
    http.post(api + 'delete', id, '删除数据').then(rs => {

      Modal.info({
        title: '删除操作', content: rs.msg, okText: '跳转到应用列表', onOk: () => {
          history.push("/admin/app")
        }
      })
    })
  }
  rename = () => {
    let id = this.state.app.id;
    http.post(api + 'rename/' + id, this.state.newName, '修改容器名称').then(rs => {
      this.setState({app: rs.data, showEditName: false})
    })
  }

  render() {
    const {app, container} = this.state;

    const {state} = container;

    return (<div>

      <Card className="panel" title={app.name} bordered size="small" extra={<Space>
        {state == 'exited' && <Button onClick={this.start} type="primary">启动</Button>}
        {state == 'running' && <Button onClick={this.stop} type="primary" danger>停止</Button>}

        <Button onClick={this.deploy} type="primary">重新部署</Button>

      </Space>}>
        <Row wrap={false}>
          <Col flex="100px">主机</Col>
          <Col flex="auto">{app.host.fullName}</Col>
        </Row>

        <Row wrap={false}>
          <Col flex="100px">镜像</Col>
          <Col flex="auto">{container.image} </Col>
        </Row>
        <Row wrap={false}>
          <Col flex="100px">容器</Col>
          <Col flex="auto">
            {container.name} <Divider type="vertical"></Divider>{container.id} <Divider
            type="vertical"></Divider>
            <Tag color={container.state == 'running' ? 'green' : 'red'}>{container.status}</Tag>
          </Col>
        </Row>
      </Card>


      {app.id && <div>
        <div className="panel">
          <Tabs tabPosition="left" defaultActiveKey="setting">
            <Tabs.TabPane tab="配置" key="2">
              <AppForm app={app}/>
            </Tabs.TabPane>


            <Tabs.TabPane tab="日志" key="container-log" className="panel">
              {container.id && <Log id={app.id} container={container}></Log>}
            </Tabs.TabPane>

            <Tabs.TabPane tab="事件" key="log" className="panel">

              {this.state.app.logUrl &&
              <div style={{minHeight: 600}}>
                <ScrollFollow
                  startFollowing={true}
                  render={({follow, onScroll}) => (
                    <LazyLog url={this.state.app.logUrl}
                             websocket
                             stream follow={follow} onScroll={onScroll}/>
                  )}
                />

              </div>}

            </Tabs.TabPane>


            <Tabs.TabPane tab="发布" key="publish" className="panel">


              <Row wrap={false}>
                <Col flex="100px">自动发布</Col>
                <Col flex="auto">
                  <Switch checked={app.autoDeploy}
                          onChange={checked => {
                            app.autoDeploy = checked
                            this.setState({app: this.state.app})
                            this.setAutoDeploy(app.id, checked)
                          }}
                  ></Switch>
                  <div className="q-mt-md">
                    <Typography.Text type="secondary">当有镜像构建成功后，自动更新应用到最新构建的版本</Typography.Text>
                  </div>
                </Col>
              </Row>
              <Divider></Divider>

              <Row wrap={false}>
                <Col flex="100px">手动发布</Col>
                <Col flex="auto">
                  <RemoteSelect url={"api/repository/tagList?url=" + app.imageUrl} style={{width: 300}}
                                placeholder="请选择"
                                showSearch
                                value={this.state.publishApp.targetVersion} onChange={targetVersion => {
                    this.setState({publishApp: {targetVersion}})
                  }}></RemoteSelect>
                  <Button type={"primary"} onClick={this.updateApp}>更新应用</Button>

                  <div className="q-mt-md">
                    <Typography.Text type="secondary">用指定的镜像版本</Typography.Text>
                  </div>
                </Col>
              </Row>

            </Tabs.TabPane>

            <Tabs.TabPane tab="迁移" key="move" className="panel">
              <Row wrap={false}>
                <Col flex="100px">迁移应用</Col>
                <Col flex="auto">
                  <Space direction={"vertical"}>
                    <Alert message="应用会迁移到下列任意一台主机中" type="warning"></Alert>


                    <RemoteSelect url="/api/host/options" style={{width: 300}} placeholder="请选择"
                                  showSearch value={this.state.moveApp.targetHostId} onChange={targetHostId => {
                      this.setState({moveApp: {targetHostId}})
                    }}></RemoteSelect>

                    <Button type={"primary"} onClick={this.moveApp}>迁移</Button>
                  </Space>
                </Col>
              </Row>
            </Tabs.TabPane>


            <Tabs.TabPane tab="设置" key="setting" className="panel"> <Divider></Divider>
              <Row wrap={false}>
                <Col flex="100px">名称</Col>
                <Col flex="auto">

                  {!this.state.showEditName ? <div>
                    {this.state.app.name} <a onClick={() => this.setState({
                    newName: this.state.app.name,
                    showEditName: true
                  })}>修改名称</a>
                  </div> : <div>

                    <Input value={this.state.newName} style={{width: 200}}
                           onChange={e => this.setState({newName: e.target.value})}></Input>

                    <Button type={"primary"} onClick={this.rename}>确定</Button>
                  </div>}


                </Col>

              </Row>
              <Divider></Divider>
              <Row wrap={false}>
                <Col flex="100px">自动重启</Col>
                <Col flex="auto">
                  <Switch checked={app.autoRestart}
                          onChange={checked => {
                            app.autoRestart = checked
                            this.setState({app: this.state.app})
                            this.setAutoRestart(app.id, checked)
                          }}
                  ></Switch>
                </Col>

              </Row>


              <Divider></Divider>
              <Row wrap={false}>
                <Col flex="100px">删除应用</Col>
                <Col flex="auto">
                  <Space direction={"vertical"}>
                    <Alert message="请注意，删除应用将清除该应用的所有数据，且该操作不能被恢复，您确定要删除吗?" type="warning"
                    ></Alert>
                    <Button danger type="primary" onClick={this.handleDelete}>删除应用</Button>
                  </Space>
                </Col>
              </Row>


            </Tabs.TabPane>
          </Tabs>


        </div>
      </div>}


    </div>)
  }


}



