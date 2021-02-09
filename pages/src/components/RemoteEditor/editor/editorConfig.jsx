const editorConfig = {
  placeholder: '请输入内容',

  // 编辑器工具栏的控件列表
  controls: [
    'headings',
    'font-size',
    'letter-spacing',
    'separator', // 分割线
    'text-color',
    'bold',
    'italic',
    'underline',
    'strike-through',
    'remove-styles',
    'separator',
    'text-align',
    'separator',
    'emoji',
    'media',
    'fullscreen'
  ],
  // 字号配置
  fontSizes: [12, 14, 16, 18, 20, 24, 28, 30, 32, 36],
  // 图片工具栏的可用控件
  imageControls: [
    'float-left', // 设置图片左浮动
    'float-right', // 设置图片右浮动
    'align-left', // 设置图片居左
    'align-center', // 设置图片居中
    'align-right', // 设置图片居右
    'link', // 设置图片超链接
    'size', // 设置图片尺寸
    'remove', // 删除图片
  ],
};
export default editorConfig;
