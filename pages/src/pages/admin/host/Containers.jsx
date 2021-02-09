import {Select, Form, Button, Card, Col, Divider, Modal, Table, Radio, Row, Space, Tabs, Input, Dropdown} from 'antd';
import React from 'react';
import http from "../../../utils/request";
import {history} from "umi";

let api = '/api/host/';


export default class extends React.Component {

  state = {
    list: []
  }

  componentDidMount() {
    this.loadData();
  }

  loadData = () => {
    let params = this.props;
    http.get(api + "containers", params).then(list => {
      this.setState({list})
    })
  }

  remove = (id) => {
    let params = this.props;
    http.get("/api/container/remove", {hostId: params.id, containerId: id}).then(this.loadData)
  }
  stop = (id) => {
    let params = this.props;
    http.get("/api/container/stop", {hostId: params.id, containerId: id}).then(this.loadData)
  }


  columns = [
    {
      title: '容器',
      dataIndex: 'Names',
      width: 200,
      render: (p, row) => {
        let params = this.props;
        if (!params) {
          return <span></span>
        }

        let hostId = params.id

        const name = row.Names[0].substr(1)
        const id = row.Id.substr(0, 12)
        return <div><a onClick={() => history.push("/admin/container/" + hostId + "/" + id)}>{name}</a><br/> {id}
        </div>
      }
    },
    {
      title: '镜像',
      dataIndex: 'Command',
      width: 360,
      ellipsis: true,
      render: (p, row) => {
        return <div>{row.Image} <br/>{row.Command}</div>
      }
    },
    {
      title: '端口(主机:容器)', dataIndex: 'Ports',
      width: 180,
      render: ps => {
        return ps.map(p => <div>{p.PublicPort}:{p.PrivatePort}/{p.Type}</div>)
      }
    },
    {title: '状态', dataIndex: 'Status', width: 180},
    {
      title: '-', dataIndex: 'action', render: (_, row) => {
        let id = row.Id;
        let state = row.State;
        let running = state == 'running';
        return <div>
          <Button onClick={() => this.stop(id)} disabled={!running} size="small">停止</Button>
          <Divider type={"vertical"}/>
          <Button onClick={() => this.remove(id)} disabled={running} danger size="small">删除</Button></div>
      }
    },

  ];

  render() {
    const {list} = this.state
    return (<div>

      <Table dataSource={list}
             bordered
             columns={this.columns}
             scroll={{x: 1000}}
      />


    </div>)
  }


}



