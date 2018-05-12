import _ from 'lodash'
const sformat = require('string-format');

// const MONTH_LIST = ["january", ""]
const DATE = (x) => x.getDate()
const YEAR = (x) => x.getYear()+1900;
const MONTH_NUM = (x) => x.getMonth()+1;
const MONTH = (obj, cap=false) => {
    const x = obj.getMonth();
    const _cap_f = (word) => cap ? (word.split("").map((ii,jj) => jj == 0 ? ii.toUpperCase() : ii).join("")) : word;
    switch(x){
        case 0: return _cap_f("january")
        case 1: return _cap_f("february")
        case 2: return _cap_f("march")
        case 3: return _cap_f("april")
        case 4: return _cap_f("may")
        case 5: return _cap_f("june")
        case 6: return _cap_f("july")
        case 7: return _cap_f("august")
        case 8: return _cap_f("september")
        case 9: return _cap_f("october")
        case 10: return _cap_f("november")
        case 11: return _cap_f("december")
        default: return ""
    }
}


const dformat = (x, options={cap: true, month_num: false, pretty: false}) => {
    const { cap, month_num, pretty } = options;
    const _date = DATE(x);
    const _month = month_num ? MONTH_NUM(x) : MONTH(x, cap)
    const _year = YEAR(x);
    if (pretty) return sformat("{0} {1}, {2}", _date, MONTH(x, cap), _year);
    return sformat("{0}/{1}/{2}", _date, _month, _year)
}

export {
    DATE,
    YEAR,
    MONTH,
    MONTH_NUM,
    dformat
}
