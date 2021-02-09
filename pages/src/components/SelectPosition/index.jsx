import {Map, Marker} from "react-amap";
import {mapKey} from "../../config";
import React from "react";

/**
 * disabled 是否允许修改
 */
class SelectPosition extends React.Component {


  state = {
    width: 600,
    height: 400,
    lng: null,
    lat: null
  }

  componentDidMount() {
    let str = this.props.value
    if (str) {
      let [lng, lat] = str.split(",")
      this.state.lng = lng;
      this.state.lat = lat;
      this.setState(this.state)
    }
  }


  render() {

    let width = this.props.width || this.state.width;
    let height = this.props.height || this.state.height;

    let disabled = this.props.disabled
    if (disabled == null) {
      disabled = false;
    }

    let center = null;
    if (this.state.lng && this.state.lat) {
      center = [this.state.lng, this.state.lat]
    }

    return <div style={{width: width, height: height}}>
      <Map zoomEnable={!disabled} center={center} dragEnable={!disabled} zoom={16} amapkey={mapKey} events={{
        click: (e) => {
          if (disabled) {
            return
          }
          const {lnglat} = e;
          const {lng, lat} = lnglat; // 经纬度
          this.state.lng = lng;
          this.state.lat = lat;
          this.setState(this.state)
          if (this.props.onChange) {
            this.props.onChange(lng + "," + lat)
          }
        }
      }}>
        {this.state.lng && this.state.lat && <Marker position={{longitude: this.state.lng, latitude: this.state.lat}}/>}
      </Map>
    </div>

  }
}

export default SelectPosition
