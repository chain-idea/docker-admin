import React from 'react';

/**
 * 使用该标签，可以判断权限到按钮级别,
 * <Perm code="user:list"></Perm>
 */
class Perm extends React.Component {

  render() {
    let {currentUser: user, code} = this.props;

    if (user != null && user.permissions != null && user.permissions.length != 0) {
      let {permissions} = user;
      if (permissions.indexOf('*') >= 0 || permissions.indexOf(code) >= 0) {
        return this.props.children || '';
      }
    }
    return <span title={'没有权限' + code}></span>
  }
}


export default Perm

