import {Button, Card, Col, Form, Descriptions, Row, Space, Switch, Tabs, Divider, Alert} from 'antd';
import React from 'react';
import http from "@/utils/request";
import Log from "./Log";
import AppForm from "./AppForm";

let api = '/api/app/';

const Item = Descriptions.Item;


export default class extends React.Component {

  state = {
    app: {
      yaml: '',
      host: {}
    },
    container: {
    }

  }

  componentDidMount() {
    let {params} = this.props.match;

    http.get(api + "get", params).then(app => {
      this.setState({app})
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

  render() {
    const {app, container} = this.state;

    const {state} = container;

    return (<div>

      <Card className="panel" title={app.name} bordered size="small" extra={<Space>
        {state == 'exited' && <Button onClick={this.start} type="primary">启动</Button>}
        {state == 'running' && <Button onClick={this.stop} type="primary" danger>停止</Button>}

        <Button onClick={this.deploy} type="primary">重新部署</Button>

      </Space>}>
        <table className="q-table-desc">
          <tbody>
          <tr>
            <th>主机</th>
            <td>{app.host.name}</td>
          </tr>
          <tr>
            <th>镜像</th>
            <td>{container.image}</td>
          </tr>
          <tr>
            <th>容器</th>
            <td>{container.name} <Divider type="vertical"></Divider>{container.id} <Divider
              type="vertical"></Divider> {container.status}</td>
          </tr>

          <tr>
            <th>端口(主机:容器)</th>
            <td> {container.ports}</td>
          </tr>
          </tbody>
        </table>
      </Card>


      {app.id && <div>
        <div className="panel">
          <Tabs tabPosition="left" defaultActiveKey="2">
            <Tabs.TabPane tab="配置" key="2">
              <AppForm app={app}/>
            </Tabs.TabPane>


            <Tabs.TabPane tab="日志" key="log">
              {container.id && <Log id={app.id} container={container}></Log>}
            </Tabs.TabPane>
            <Tabs.TabPane tab="事件" key="event">
              TODO 时间线记录， 启动，部署等
            </Tabs.TabPane>

            <Tabs.TabPane tab="设置" key="6">
              <Row>
                <Col flex="100px"> 自动发布</Col>
                <Col>
                  <Switch checked={app.autoDeploy}
                          onChange={checked => {
                            app.autoDeploy = checked
                            this.setState({app: this.state.app})
                            this.setAutoDeploy(app.id, checked)
                          }}
                  ></Switch>
                </Col>
              </Row>

            </Tabs.TabPane>
          </Tabs>


        </div>
      </div>}


    </div>)
  }


}



