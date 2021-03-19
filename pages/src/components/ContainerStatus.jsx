import {Space, Input, Button, Form, Table, InputNumber, Select, Tag} from "antd";
import React from "react";
import {
  DeleteFilled,
  DeleteOutlined,
  MinusCircleOutlined,
  PlusCircleFilled,
  PlusCircleOutlined,
  PlusOutlined
} from '@ant-design/icons';
import http from "../utils/request";

/**
 * 容器状态
 */
export default class extends React.Component {

  state = {
    status: '-'
  }

  componentDidMount() {
    const {hostId, appName} = this.props
    http.get("api/container/status", {hostId, appName},false).then(rs => {
      this.setState({status: rs.msg})
    })
  }

  render() {
    const s = this.state.status;
    if (s && s.indexOf('Up') >= 0) {
      return <Tag color={"green"}>{s} </Tag>
    }
    return <Tag color={"red"}>{s}</Tag>
  }
}
