/**
 权限相关的功能代码
 **/


const auth = {
  isLogin() {
    return localStorage.getItem('isLogin') == 'true'
  },
  setIsLogin(result) {
    localStorage.setItem('isLogin', result)
  }
}

export default auth
