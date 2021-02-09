
const getTableFormProps = (initValues) => {
  return {
    form: {
      layout: 'horizontal',
      labelCol: {span: 5},
      initialValues: initValues
    },
    type: "form",
    rowSelection: false,

  }
}
const common = {
  getTableFormProps
}
export default common
