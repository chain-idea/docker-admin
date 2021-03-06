import {Select, Form, Button, Card, Col, Divider, Modal, Radio, Row, Space, Tabs, Input, Badge} from 'antd';
import React from 'react';
import http from "../../../utils/request";
import Containers from "./Containers";
import Images from "./Images";

let api = '/api/host/';

const dict = {
  architecture: '架构',
  operatingSystem: '操作系统',
  systemTime: '系统时间',
  serverVersion: 'docker版本',
  id: 'dockerId',
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

  save = value => {
    value.id = this.state.host.id
    http.post(api + 'update', value)
  }


  render() {
    const {host, info} = this.state;
    return (<div>
      <div className="panel">
        <div>
          <Row><Col flex="100px">主机名</Col> <Col> {host.fullName}</Col> </Row>
          <Row><Col flex="100px">内存</Col> <Col>

            {(info.memTotal / 1024 / 1024 / 1024).toFixed(1)} G

          </Col></Row>
          {
            keys.map(k => <Row key={k}><Col flex="100px">{dict[k]}</Col> <Col> {info[k]}</Col> </Row>)
          }
        </div>

      </div>

      {host.id && <div className="panel">
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab={<Badge count={info.containers} size={"small"}> 容器</Badge>} key="1">
            <Containers id={host.id}></Containers>
          </Tabs.TabPane>
          <Tabs.TabPane tab={<Badge count={info.images} size={"small"}> 镜像</Badge>} key="2">
            <Images id={host.id}></Images>

          </Tabs.TabPane>
          <Tabs.TabPane tab="设置" key="setting">


            <Form initialValues={host} labelCol={{span: 3}} onFinish={this.save}>
              <Form.Item name={"name"} label={"主机名"}
                         rules={[{required: true}, {pattern: /^[a-zA-Z-0-9|_|\-]+$/, message: '字母、数字、下划线'}]}>
                <Input></Input>
              </Form.Item>
              <Form.Item name={"remark"} label={"备注"}>
                <Input></Input>
              </Form.Item>
              <Form.Item wrapperCol={{offset: 3}}>
                <Button htmlType={"submit"} type={"primary"}>保存修改</Button>
              </Form.Item>

            </Form>


          </Tabs.TabPane>

        </Tabs>
      </div>

      }

    </div>)
  }


}



