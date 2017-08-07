package com.jhjj9158.niupaivideo.bean;

import java.util.List;

/**
 * Created by oneki on 2017/5/19.
 */

public class GoogleLocationBean {
    /**
     * results : [{"address_components":[{"long_name":"365","short_name":"365","types":["street_number"]},{"long_name":"丰潭路",
     * "short_name":"丰潭路","types":["route"]},{"long_name":"古墩路","short_name":"古墩路","types":["neighborhood","political"]},
     * {"long_name":"拱墅区","short_name":"拱墅区","types":["political","sublocality","sublocality_level_1"]},{"long_name":"杭州市",
     * "short_name":"杭州市","types":["locality","political"]},{"long_name":"浙江省","short_name":"浙江省","types":["administrative_area_level_1",
     * "political"]},{"long_name":"中国","short_name":"CN","types":["country","political"]},{"long_name":"310000","short_name":"310000",
     * "types":["postal_code"]}],"formatted_address":"中国浙江省杭州市拱墅区古墩路丰潭路365号 邮政编码: 310000","geometry":{"location":{"lat":30.302577,
     * "lng":120.105166},"location_type":"ROOFTOP","viewport":{"northeast":{"lat":30.3039259802915,"lng":120.1065149802915},
     * "southwest":{"lat":30.3012280197085,"lng":120.1038170197085}}},"place_id":"ChIJw6k1P7ZjSzQRjwt57bSGYm0",
     * "types":["street_address"]},{"address_components":[{"long_name":"古墩路","short_name":"古墩路","types":["neighborhood","political"]},
     * {"long_name":"杭州市","short_name":"杭州市","types":["locality","political"]},{"long_name":"浙江省","short_name":"浙江省",
     * "types":["administrative_area_level_1","political"]},{"long_name":"中国","short_name":"CN","types":["country","political"]},
     * {"long_name":"310000","short_name":"310000","types":["postal_code"]}],"formatted_address":"中国浙江省杭州市古墩路 邮政编码: 310000",
     * "geometry":{"bounds":{"northeast":{"lat":30.3192429,"lng":120.1060183},"southwest":{"lat":30.2998606,"lng":120.090385}},
     * "location":{"lat":30.306973,"lng":120.096756},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.3192429,
     * "lng":120.1060183},"southwest":{"lat":30.2998606,"lng":120.090385}}},"place_id":"ChIJz23AisZjSzQRSloIBXLwr-c",
     * "types":["neighborhood","political"]},{"address_components":[{"long_name":"拱墅区","short_name":"拱墅区","types":["political",
     * "sublocality","sublocality_level_1"]},{"long_name":"杭州市","short_name":"杭州市","types":["locality","political"]},{"long_name":"浙江省",
     * "short_name":"浙江省","types":["administrative_area_level_1","political"]},{"long_name":"中国","short_name":"CN","types":["country",
     * "political"]}],"formatted_address":"中国浙江省杭州市拱墅区","geometry":{"bounds":{"northeast":{"lat":30.3962476,"lng":120.2213045},
     * "southwest":{"lat":30.2726934,"lng":120.0858132}},"location":{"lat":30.319037,"lng":120.141405},"location_type":"APPROXIMATE",
     * "viewport":{"northeast":{"lat":30.3962476,"lng":120.2213045},"southwest":{"lat":30.2726934,"lng":120.0858132}}},
     * "place_id":"ChIJ41QixdhhSzQRbBSzpDoS7Bo","types":["political","sublocality","sublocality_level_1"]},
     * {"address_components":[{"long_name":"杭州市","short_name":"杭州市","types":["locality","political"]},{"long_name":"浙江省",
     * "short_name":"浙江省","types":["administrative_area_level_1","political"]},{"long_name":"中国","short_name":"CN","types":["country",
     * "political"]}],"formatted_address":"中国浙江省杭州市","geometry":{"bounds":{"northeast":{"lat":30.5665162,"lng":120.7219451},
     * "southwest":{"lat":29.18875689999999,"lng":118.3449333}},"location":{"lat":30.274084,"lng":120.15507},
     * "location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.4743515,"lng":120.4307556},"southwest":{"lat":30.0484295,
     * "lng":119.9130249}}},"place_id":"ChIJmaqaQym2SzQROuhNgoPRv6c","types":["locality","political"]},
     * {"address_components":[{"long_name":"310012","short_name":"310012","types":["postal_code"]},{"long_name":"西湖区","short_name":"西湖区",
     * "types":["political","sublocality","sublocality_level_1"]},{"long_name":"杭州市","short_name":"杭州市","types":["locality",
     * "political"]},{"long_name":"浙江省","short_name":"浙江省","types":["administrative_area_level_1","political"]},{"long_name":"中国",
     * "short_name":"CN","types":["country","political"]}],"formatted_address":"中国浙江省杭州市西湖区 邮政编码: 310012",
     * "geometry":{"bounds":{"northeast":{"lat":30.3344746,"lng":120.1589304},"southwest":{"lat":30.2541963,"lng":120.0665237}},
     * "location":{"lat":30.3227072,"lng":120.0855949},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.3344746,
     * "lng":120.1589304},"southwest":{"lat":30.2541963,"lng":120.0665237}}},"place_id":"ChIJz8I97OZjSzQRIqdOdvxCDd4",
     * "types":["postal_code"]},{"address_components":[{"long_name":"310000","short_name":"310000","types":["postal_code"]},
     * {"long_name":"拱墅区","short_name":"拱墅区","types":["political","sublocality","sublocality_level_1"]},{"long_name":"杭州市",
     * "short_name":"杭州市","types":["locality","political"]},{"long_name":"浙江省","short_name":"浙江省","types":["administrative_area_level_1",
     * "political"]},{"long_name":"中国","short_name":"CN","types":["country","political"]}],"formatted_address":"中国浙江省杭州市拱墅区 邮政编码:
     * 310000","geometry":{"bounds":{"northeast":{"lat":30.3947548,"lng":120.2714643},"southwest":{"lat":30.227433,"lng":120.0665237}},
     * "location":{"lat":30.2734437,"lng":120.155262},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":30.3947548,
     * "lng":120.2714643},"southwest":{"lat":30.227433,"lng":120.0665237}}},"place_id":"ChIJYUMa8H5iSzQRgQOuD4qXECw",
     * "types":["postal_code"]},{"address_components":[{"long_name":"浙江省","short_name":"浙江省","types":["administrative_area_level_1",
     * "political"]},{"long_name":"中国","short_name":"CN","types":["country","political"]}],"formatted_address":"中国浙江省",
     * "geometry":{"bounds":{"northeast":{"lat":31.1787819,"lng":122.9495085},"southwest":{"lat":27.0413557,"lng":118.0282788}},
     * "location":{"lat":29.1416432,"lng":119.7889248},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":31.1787819,
     * "lng":122.9491197},"southwest":{"lat":27.0416124,"lng":118.0282788}}},"place_id":"ChIJv8wKpTQSSTQRt3wYNsVx74E",
     * "types":["administrative_area_level_1","political"]},{"address_components":[{"long_name":"中国","short_name":"CN",
     * "types":["country","political"]}],"formatted_address":"中国","geometry":{"bounds":{"northeast":{"lat":53.56097399999999,
     * "lng":134.7728099},"southwest":{"lat":17.9996,"lng":73.4994136}},"location":{"lat":35.86166,"lng":104.195397},
     * "location_type":"APPROXIMATE","viewport":{"northeast":{"lat":53.56097399999999,"lng":134.7726951},"southwest":{"lat":18.1618062,
     * "lng":73.5034261}}},"place_id":"ChIJwULG5WSOUDERbzafNHyqHZU","types":["country","political"]}]
     * status : OK
     */

    private String status;
    private List<ResultsBean> results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * address_components : [{"long_name":"365","short_name":"365","types":["street_number"]},{"long_name":"丰潭路","short_name":"丰潭路",
         * "types":["route"]},{"long_name":"古墩路","short_name":"古墩路","types":["neighborhood","political"]},{"long_name":"拱墅区",
         * "short_name":"拱墅区","types":["political","sublocality","sublocality_level_1"]},{"long_name":"杭州市","short_name":"杭州市",
         * "types":["locality","political"]},{"long_name":"浙江省","short_name":"浙江省","types":["administrative_area_level_1","political"]},
         * {"long_name":"中国","short_name":"CN","types":["country","political"]},{"long_name":"310000","short_name":"310000",
         * "types":["postal_code"]}]
         * formatted_address : 中国浙江省杭州市拱墅区古墩路丰潭路365号 邮政编码: 310000
         * geometry : {"location":{"lat":30.302577,"lng":120.105166},"location_type":"ROOFTOP",
         * "viewport":{"northeast":{"lat":30.3039259802915,"lng":120.1065149802915},"southwest":{"lat":30.3012280197085,
         * "lng":120.1038170197085}}}
         * place_id : ChIJw6k1P7ZjSzQRjwt57bSGYm0
         * types : ["street_address"]
         */

        private String formatted_address;
        private GeometryBean geometry;
        private String place_id;
        private List<AddressComponentsBean> address_components;
        private List<String> types;

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public GeometryBean getGeometry() {
            return geometry;
        }

        public void setGeometry(GeometryBean geometry) {
            this.geometry = geometry;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public List<AddressComponentsBean> getAddress_components() {
            return address_components;
        }

        public void setAddress_components(List<AddressComponentsBean> address_components) {
            this.address_components = address_components;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public static class GeometryBean {
            /**
             * location : {"lat":30.302577,"lng":120.105166}
             * location_type : ROOFTOP
             * viewport : {"northeast":{"lat":30.3039259802915,"lng":120.1065149802915},"southwest":{"lat":30.3012280197085,
             * "lng":120.1038170197085}}
             */

            private LocationBean location;
            private String location_type;
            private ViewportBean viewport;

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public String getLocation_type() {
                return location_type;
            }

            public void setLocation_type(String location_type) {
                this.location_type = location_type;
            }

            public ViewportBean getViewport() {
                return viewport;
            }

            public void setViewport(ViewportBean viewport) {
                this.viewport = viewport;
            }

            public static class LocationBean {
                /**
                 * lat : 30.302577
                 * lng : 120.105166
                 */

                private double lat;
                private double lng;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }
            }

            public static class ViewportBean {
                /**
                 * northeast : {"lat":30.3039259802915,"lng":120.1065149802915}
                 * southwest : {"lat":30.3012280197085,"lng":120.1038170197085}
                 */

                private NortheastBean northeast;
                private SouthwestBean southwest;

                public NortheastBean getNortheast() {
                    return northeast;
                }

                public void setNortheast(NortheastBean northeast) {
                    this.northeast = northeast;
                }

                public SouthwestBean getSouthwest() {
                    return southwest;
                }

                public void setSouthwest(SouthwestBean southwest) {
                    this.southwest = southwest;
                }

                public static class NortheastBean {
                    /**
                     * lat : 30.3039259802915
                     * lng : 120.1065149802915
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }

                public static class SouthwestBean {
                    /**
                     * lat : 30.3012280197085
                     * lng : 120.1038170197085
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }
            }
        }

        public static class AddressComponentsBean {
            /**
             * long_name : 365
             * short_name : 365
             * types : ["street_number"]
             */

            private String long_name;
            private String short_name;
            private List<String> types;

            public String getLong_name() {
                return long_name;
            }

            public void setLong_name(String long_name) {
                this.long_name = long_name;
            }

            public String getShort_name() {
                return short_name;
            }

            public void setShort_name(String short_name) {
                this.short_name = short_name;
            }

            public List<String> getTypes() {
                return types;
            }

            public void setTypes(List<String> types) {
                this.types = types;
            }
        }
    }
}
