import {Button, Form, Input, Divider} from 'antd';
import React from 'react';
import http from "../../../utils/request";
import RemoteSelect from "../../../components/RemoteSelect";

let api = '/api/repository/';
import {history} from "umi";

export default class extends React.Component {

  state = {
    current: 0,
    url: null,
  }

  constructor(props) {
    super(props);
    this.state.url = props.location.query.url
  }

  save = () => {
    this.appFormRef.current.validateFields().then(vs => {
      vs.yaml = {
        restart: vs.restart ? 'always' : 'no'
      }
      http.post("/api/app/save", vs).then(rs => {
        history.push("/admin/app/" + rs.id)
      });
    })

  }

  appFormRef = React.createRef()


  render() {
    const {url} = this.state

    return (<div className="panel">

      <h2>基本信息</h2>
      <Divider></Divider>
      <Form ref={this.appFormRef} labelCol={{span: 4}} initialValues={{imageUrl: url}}>
        <Form.Item name="appName" label="应用名称" wrapperCol={{span: 8}}>
          <Input></Input>
        </Form.Item>
        <Form.Item label="镜像" name="imageUrl">
          <Input readOnly={true} disabled={true}></Input>
        </Form.Item>
        <Form.Item label="版本号" name="imageTag" wrapperCol={{span: 5}}>
          <RemoteSelect url={"/api/repository/tagList?url=" + this.state.url}></RemoteSelect>
        </Form.Item>
        <Form.Item label="运行环境" name="hostId" wrapperCol={{span: 5}}>
          <RemoteSelect url="/api/host/options"></RemoteSelect>
        </Form.Item>


        <Form.Item wrapperCol={{offset: 4}}>
          <Button type="primary" onClick={this.save}> 确定 </Button>
        </Form.Item>
      </Form>


    </div>)
  }


}



