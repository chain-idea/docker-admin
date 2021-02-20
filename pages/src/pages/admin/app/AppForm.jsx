import {Row, Button, Form, Col, Divider} from "antd";
import React from "react";
import EditTable from "../../../components/EditTable";
import http from "../../../utils/request";


export default class extends React.Component {

  state = {
    form: {
      ports: [],
      binds: [],
      environment: []
    }
  }

  constructor(props) {
    super(props);

    let config = this.props.app.config;
    if (config) {
      this.state.form = config
    }
  }


  portsColumns = [
    {title: '主机端口', dataIndex: 'publicPort', dataType: 'InputNumber'},
    {title: '容器端口', dataIndex: 'privatePort', dataType: 'InputNumber'},
    {title: '协议', dataIndex: 'protocol', dataType: 'Select', valueEnum: {TCP: 'TCP', UDP: 'UDP'}},
  ]

  bindsColumns = [
    {title: '主机路径', dataIndex: 'publicVolume', dataType: 'Input'},
    {title: '容器路径', dataIndex: 'privateVolume', dataType: 'Input'},
    {title: '只读', dataIndex: 'readOnly', dataType: 'Select', valueEnum: {true: '只读', false: '读写'}},
  ]

  envColumns = [
    {title: '键', dataIndex: 'key', dataType: 'Input'},
    {title: '值', dataIndex: 'value', dataType: 'Input'},
  ]

  update = () => {
    const {form} = this.state;
    http.post('/api/app/updateConfig?id=' + this.props.app.id, form)
  }

  render() {
    const {form} = this.state;
    return <div>

      <Row>
        <Col flex="100px">
          <h4>端口绑定</h4>
        </Col>
        <Col flex="auto">
          <EditTable columns={this.portsColumns} dataSource={form.ports}></EditTable>
        </Col>
      </Row>
      <Divider/>

      <Row>
        <Col flex="100px">
          <h4>卷映射</h4>
        </Col>
        <Col flex="auto">
          <EditTable columns={this.bindsColumns} dataSource={form.binds}></EditTable>
        </Col>
      </Row>
      <Divider/>

      <Row>
        <Col flex="100px">
          <h4>环境变量</h4>
        </Col>
        <Col flex="auto">
          <EditTable columns={this.envColumns} dataSource={form.environment}></EditTable>
        </Col>
      </Row>


      <Divider/>

      <Row>
        <Col flex="100px">
        </Col>
        <Col flex="auto">
          <Button type="primary" onClick={this.update}>保存更改</Button>
        </Col>
      </Row>


    </div>

  }
}
