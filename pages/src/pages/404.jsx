import React from 'react';
import styles from './index.less';
import {Button, Input} from 'antd'
import {history} from 'umi'
export default () => {
  return (
    <div>
      404，页面没有找到

      <Button onClick={()=>history.push('/')}>返回首页</Button>
    </div>
  );
}
