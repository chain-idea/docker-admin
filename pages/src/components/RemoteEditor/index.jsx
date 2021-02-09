import React, { Component } from 'react';
import BraftEditor from 'braft-editor';
import 'braft-editor/dist/index.css';
import PropTypes from 'prop-types';
import {Button, message, Upload} from 'antd';
import {fileUrl} from "../../config";
import './editor/style.less'; // 见下文
import config from './editor/editorConfig'; // 见下文
import mediaBaseconfig from './editor/media'; // 见下文
import { isImageFile, maxFileSize, uploadImage } from './editor/mediaUpload';
import {UploadOutlined} from "@ant-design/icons";

class RemoteEditor extends Component {
  state = {
    // 创建一个空的editorState作为初始值
    editorState: BraftEditor.createEditorState(this.props.value),
    contentId:'',
  };

  static propTypes = {
    // 富文本初始内容
    html: PropTypes.string,
    //  限制图片文件大小
    fileMaxSize: PropTypes.number
  };

  componentDidMount() {

  }

  componentDidUpdate(prevProps) {
    const {contentId} = this.props
    if (this.props.contentId !== prevProps.contentId) {
      this.setState({
        contentId,
        editorState: BraftEditor.createEditorState(prevProps.value),
      })
    }
  }

  // 默认的props
  static defaultProps = {
    fileMaxSize: 5 // 单位默认是Mb,
  };
  // 监听编辑器内容变化同步内容到state
  handleEditorChange = editorState => {
    const htmlString = editorState.toHTML();
    this.setState({ editorState: editorState }, () => {
      this.props.onChange(htmlString)
    })
  };

  // 媒体上传校验
  mediaValidate = file => {
    const { fileMaxSize } = this.props;
    // 类型限制
    if (!isImageFile(file)) {
      return false;
    }
    // 大小限制
    if (fileMaxSize && !maxFileSize(file, fileMaxSize)) {
      return false;
    }
    return true;
  };

  // 媒体上传
  mediaUpload = async (param) => {
    message.destroy();
    try {
      const url = await uploadImage(param);
      success({ url });
      message.success('上传成功');
    } catch (err) {
      console.log(err);
      error(err);
      message.error(err.message || '上传失败');
    }
  };

  //预览
  preview = () => {

    if (window.previewWindow) {
      window.previewWindow.close()
    }

    window.previewWindow = window.open()
    window.previewWindow.document.write(this.buildPreviewHtml())
    window.previewWindow.document.close()

  };


  importFile = () => {
    const uploadBtn = document.querySelector(".uploadBtn");
    window.a = uploadBtn;
    window.a.click();
  };

  handleImportChange = (info) => {
    const {status} = info.file;
    if (status !== 'uploading') {
      console.log(info.file, info.fileList);
    }
    if (status === 'done') {
      if (info.file.response.error) {
        message.error("文件内容格式错误，请检查文件格式！");
      } else {
        let htmlString = info.file.response.msg;
        htmlString = htmlString.replace(/width:.*?;/, "").replace(/height:.*?;/,"");
        let editorState = BraftEditor.createEditorState(htmlString);
        this.setState({ editorState: editorState }, () => {
          this.props.onChange(htmlString)
        });
        message.success({ content: '上传完成！', duration: 3 });
      }
    } else if (status === 'error') {
      message.error(`${info.file.name} 上传失败！`);
    }
  }

  buildPreviewHtml () {
    return `
      <!Doctype html>
      <html>
        <head>
          <title>Preview Content</title>
          <style>
            html,body{
              height: 100%;
              margin: 0;
              padding: 0;
              overflow: auto;
              background-color: #f1f2f3;
            }
            .container{
              box-sizing: border-box;
              width: 1000px;
              max-width: 100%;
              min-height: 100%;
              margin: 0 auto;
              padding: 30px 20px;
              overflow: hidden;
              background-color: #fff;
              border-right: solid 1px #eee;
              border-left: solid 1px #eee;
            }
            .container img,
            .container audio,
            .container video{
              max-width: 100%;
              height: auto;
            }
            .container p{
              white-space: pre-wrap;
              min-height: 1em;
            }
            .container pre{
              padding: 15px;
              background-color: #f1f1f1;
              border-radius: 5px;
            }
            .container blockquote{
              margin: 0;
              padding: 15px;
              background-color: #f1f1f1;
              border-left: 3px solid #d1d1d1;
            }
          </style>
        </head>
        <body>
          <div class="container">${this.state.editorState.toHTML()}</div>
        </body>
      </html>
    ` }

    checkType = (file, typeList) =>{
      return new Promise(function (resolve, reject) {
        if (!typeList.includes(file.type)) {
          message.error('文件类型错误，请重新上传');
          reject()
        } else {
          resolve()
        }
      });
    }

  render() {
    const { editorState } = this.state;
    const {disabled} = this.props;
    // 媒体配置
    const media = {
      // 上传校验
      validateFn: this.mediaValidate,
      // 上传
      uploadFn: this.mediaUpload,
      // 基本配置
      ...mediaBaseconfig,
    };
    const extendControls = [
      {
        key: 'custom-button',
        type: 'button',
        text: '预览',
        onClick: this.preview
      },
      {
        key: 'button-import',
        type: 'button',
        text: '数据导入',
        title: '仅支持.doc和.docx格式',
        onClick: this.importFile
      }
    ];
    return (
      <>
        <BraftEditor
          style={{
            height: 600,
            overflow: 'hidden',
          }}
          {...config}
          media={media}
          value={editorState}
          contentFormat={"html"}
          onChange={this.handleEditorChange}
          extendControls={extendControls}
          readOnly={disabled}
        />



        <div style={{display:"none"}}>
          <Upload
            action={fileUrl +"/api/file/web/wordToHtml"}
            showUploadList={false}
            headers={{'Authorization': localStorage.getItem("jwt")}}
            listType='text'
            multiple={true}
            onChange={this.handleImportChange}
            accept=".doc,.docx"
            beforeUpload={(file, fileList) => {
              message.loading({ content: '文件上传中...'});
              return Promise.all([
                this.checkType(file, ['application/msword','application/vnd.openxmlformats-officedocument.wordprocessingml.document']),
              ])
            }}
          >
            <Button type="primary" className={"uploadBtn"}><UploadOutlined /> 上传文档 </Button>
          </Upload>
        </div>

        </>

    );
  }
}

export default RemoteEditor;
