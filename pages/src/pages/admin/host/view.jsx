import {Select, Form, Button, Card, Col, Divider, Modal, Radio, Row, Space, Tabs, Input} from 'antd';
import React from 'react';
import http from "../../../utils/request";
import Containers from "./Containers";
import Images from "./Images";

let api = '/api/host/';

const dict = {
  Images: '镜像数量',
  Architecture: '架构',
  ContainersRunning: '运行容器',
  KernelVersion: '内核',
  MemTotal: '内存',
  NCPU: 'NCPU',
  OperatingSystem: '操作系统',
  SystemTime: '系统时间',
  ServerVersion: '版本',
}
const keys = Object.keys(dict)

export default class extends React.Component {

  state = {
    host: {},
    info: {}
  }

  componentDidMount() {
    let {params} = this.props.match;

    http.get(api + "get", params).then(result => {
      this.setState({...result})
    })
  }


  render() {
    const {host, info} = this.state;
    return (<div>
      <div className="panel">
        <div>
          {
            keys.map(k => <Row><Col fle>{dict[k]} :</Col> <Col>{info[k]}</Col> </Row>)
          }
        </div>

      </div>

      {host.id && <div className="panel">
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="容器" key="1">
            <Containers id={host.id}></Containers>
          </Tabs.TabPane>
          <Tabs.TabPane tab="镜像" key="2">
            <Images id={host.id}></Images>

          </Tabs.TabPane>

        </Tabs>
      </div>

      }

    </div>)
  }


}



