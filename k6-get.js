import {check} from "k6";
import http from "k6/http";

export const options = {
    discardResponseBodies: true,
    // define thresholds
    thresholds: {
        'http_req_duration{scenario:Loading}': [{ threshold: 'p(99)<1000', abortOnFail: false }],
        'http_reqs{scenario:Loading}' : [{ threshold: 'count>1000', abortOnFail: false }]
    },
    // define scenarios
    scenarios: {
        Warmup: {
            exec: 'GetDictionaries',
            executor: "ramping-vus",
            startVUs: 1,
            stages: [
                // target - virtual users/requests
                {duration: "60s", target: 40},
            ],
            gracefulRampDown: '1s',
            gracefulStop: '1s',
            startTime: '1s',
        },
        Pause: {
            exec: 'GetDictionaries',
            executor: "ramping-vus",
            startVUs: 0,
            stages: [
                // target - virtual users/requests
                {duration: "8s", target: 0},
                {duration: "2s", target: 1},
            ],
            gracefulRampDown: '1s',
            gracefulStop: '1s',
            startTime: '61s',
        },
        Loading: {
            exec: 'GetDictionaries',
            executor: "ramping-vus",
            startVUs: 1,
            stages: [
                // target - virtual users/requests
                {duration: "70s", target: 120},
                {duration: "20s", target: 120},
            ],
            startTime: '71s',
        },
    },
};


const url = `http://${__ENV.TARGET_HOST}/api/v1/dictionary`;
const params = {
    headers: {
        'Authorization': 'Bearer EXPECTED_JWT_TOKEN_IN_AUTH_HEADER_HERE',
    },
};

const categories = [EXPECTED_DICTIONARIES_CATEGORIES_JSON_LIST_CONTENT_HERE];
const categories_length = categories.length;

export function GetDictionaries() {
    // define URL and request body
    const category = categories[Math.floor(Math.random() * categories_length)];

    // send a get request and save response as a variable
    const response = http.get(`${url}?category=${encodeURIComponent(category)}`, params);

    // check response
    check(response, {
        "response code was 200": (res) => res.status === 200,
    });
}
