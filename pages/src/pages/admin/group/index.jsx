import {serverUrl} from "../../../config";
import ProTable, { EditableProTable } from '@ant-design/pro-table';
import http from "../../../utils/request";
import React from "react";

let api = '/api/group/';
let prefix = serverUrl;
if (prefix.endsWith("/")) {
  prefix = prefix.substr(0, prefix.length - 1);
}

export default class extends React.Component {

  actionRef = React.createRef();
  state = {
    editableKeys: []
  };
  columns = [
    {
      title: '分组名字',
      dataIndex: 'groupName',
      formItemProps: {
        rules: [
          {
            required: true,
            message: '此项为必填项',
          },
        ]
      },
      width: '30%',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: '30%',
      editable: false
    },
    {
      title: '修改时间',
      dataIndex: 'modifyTime',
      width: '30%',
      editable: false
    },
    {
      title: '操作',
      valueType: 'option',
      width: 200,
      render: (text, record, _, action) => [
        <a
          key="editable"
          onClick={() => {
            action.startEditable?.(record.id);
          }}
        >
          操作
        </a>,
      ],
    }
  ];

  saveGroupData(editableKeys,rows){
    let params ={
      id: rows.id,
      groupName: rows.groupName
    };
    http.post(api + 'saveOrUpdateGroup', params).then(rs => {

      // this.loadTableDate
      this.setState(this.state);
    });
    this.actionRef.current.reload();
  }
  deleteRows(id){
    http.post(  api + 'deleteGroupById?id=' +id ).then(rs =>{
      this.setState(this.state);
    });
    this.actionRef.current.reload();
  }
  render() {

    return (<div>
      <div className="panel">
          <EditableProTable
            actionRef={ this.actionRef }
            rowKey="id"
            headerTitle="分组管理"
            columns={ this.columns }
            request={ (params, sort) => http.getPageableData(api + 'list', params, sort) }
            editable={{
              type: 'multiple',
              onSave: this.saveGroupData.bind(this),
              onDelete: this.deleteRows.bind(this),
            }}
          />

      </div>
    </div>)
  }


}



