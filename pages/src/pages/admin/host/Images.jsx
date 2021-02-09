import {Select, message,Form, Button, Card, Col, Divider, Modal, Table, Radio, Row, Space, Tabs, Input} from 'antd';
import React from 'react';
import http from "../../../utils/request";

let api = '/api/host/';


export default class extends React.Component {

  state = {
    list: []
  }

  componentDidMount() {

    this.loadData();
  }

  loadData() {
    let params = this.props;
    http.get(api + "images", params).then(list => {
      this.setState({list})
    })
  }

  delete = imageId => {
    let params = this.props;
    http.get(api + "/deleteImage", {id: params.id, imageId}).then(rs => {
      this.loadData()
    })
  }

  columns = [
    {
      title: '镜像',
      dataIndex: 'RepoTags',
      width: 400,
      render(tags, row) {

        return <div>{tags} <br/> {row.Id.substr(7, 12)}</div>
      }
    },
    {
      title: '创建于',
      dataIndex: 'Created',
      width: 400,
      render: function (v, row) {
        let date = new Date(v * 1000);
        return date.toLocaleDateString();
      }
    },
    {
      title: '大小',
      dataIndex: 'Size',
      render(v) {
        return (v / 1024 / 1024).toFixed(1) + " MB";
      }
    },

    {
      dataIndex: 'action',
      render: (_,row)=> {
        return <Button onClick={()=>this.delete(row.Id)}>删除</Button>;
      }
    },
  ];

  render() {
    const {list} = this.state
    return (<div>

      <Table dataSource={list}
             bordered
             rowKey="Id"
             columns={this.columns}
             scroll={{x: 1000}}
      />


    </div>)
  }


}



