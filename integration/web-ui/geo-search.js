// --------------------------------------------------------------------------------

import('/webjars/sockjs-client/sockjs.min.js')
import('/webjars/stomp-websocket/stomp.min.js');
import('/webjars/jquery/jquery.min.js');

const onConnected = () => {
    console.log("connected");
    stompClient.subscribe(
        "/user/queue/geo_search/result",
        (msg) => {
            console.log(JSON.parse(msg.body))
        }
    );
};

const onError = (err) => {
    console.log(err);
};

// --------------------------------------------------------------------------------

SockJS = new SockJS("http://localhost:8080/ws");
stompClient = Stomp.over(SockJS);
stompClient.connect({}, onConnected, onError);

// --------------------------------------------------------------------------------

stompClient.send("/app/geo_search", {}, JSON.stringify({
    "type": "Polygon",
    "coordinates": [[
        [23.99, 50.48],
        [22.14, 48.54],
        [23.02, 47.98],
        [26.05, 47.93],
        [27.42, 48.45],
        [29.26, 47.87],
        [29.97, 46.55],
        [28.30, 45.58],
        [29.66, 45.30],
        [30.98, 46.70],
        [32.38, 46.61],
        [33.48, 45.92],
        [32.60, 45.58],
        [34.01, 44.33],
        [36.73, 45.27],
        [34.27, 45.98],
        [38.80, 47.30],
        [39.99, 48.13],
        [40.16, 49.52],
        [37.52, 50.34],
        [35.63, 50.54],
        [34.27, 51.64],
        [33.92, 52.24],
        [32.29, 52.32],
        [30.49, 51.39],
        [27.72, 51.56],
        [25.40, 52.02],
        [23.68, 51.64],
        [23.99, 50.48]
    ]]
}));

// --------------------------------------------------------------------------------
