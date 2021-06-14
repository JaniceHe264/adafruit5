class StringUtil {
    /**
     * 判断是否为字符串
     * @param {*} object
     * @return {Boolean} isString
     */
    static isString(object) {
        return (object instanceof String || typeof object === 'string' || (this.isNotNull(object) && object.constructor === String));
    };

    /**
     * 判断字符串是否为空
     * @param {String} str
     *
     */
    static isEmpty(str) {
        return !!(this.isNull(str) || (this.isString(str) && str.trim() === ""));
    }

    /**
     * 判断字符串是否不为空
     * @param {String} str
     *
     */
    static isNotEmpty(str) {
        return !this.isEmpty(str);
    }

    /**
     * 判断是否为整数,默认验证非负整数
     * @param {String} str
     * @param positive
     * @return {Boolean} isNumber
     */
    static isInt(str, positive = true) {
        let exp = "^[0-9]*$";
        if (positive === true) {
            exp = "^\\d+$";
        } else if (positive === false) {
            exp = "^((-\\d+)|(0+))$";
        }
        let reg = new RegExp(exp);
        return reg.test(str)
    };


    /**
     * 判断是否为浮点数,默认验证非负浮点数
     * @param {String} str
     * @param {Boolean} positive 是否为正数,true验证非负浮点,false验证非正浮点
     * @return {Boolean}
     */
    static isFloat(str, positive = true) {
        let exp = "^(-?\\d+)(\\.\\d+)?$";
        if (positive === true) {
            exp = "^\\d+(\\.\\d+)?$";
        } else if (positive === false) {
            exp = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";
        }
        const reg = new RegExp(exp);
        return reg.test(str)
    };

    /**
     * 判断是否为数字(整数或浮点数),默认验证非负数
     * @param {String} str
     * @param {Boolean} positive 是否为正数,true验证非负浮点,false验证非正浮点
     * @return {Boolean}
     */
    static isNumber(str, positive = true) {
        //先验证是否为整数
        let valid = this.isInt(str, positive);
        //若不为整数,再验证是否为浮点数
        if (valid === false) {
            valid = this.isFloat(str, positive);
        }
        return valid;
    }

    /**
     * 判断是否为数字(整数或浮点数),不区分正负
     * @param {String} str
     * @return {Boolean}
     */
    static isNumberUnsigned(str) {
        let valid = this.isNumber(str, true);
        if (valid === false) {
            valid = this.isNumber(str, false);
        }
        return valid;
    }

    /**
     * 判断是否为null或undefined
     */
    static isNull(obj) {
        return obj === null || obj === undefined;
    }

    /**
     * 判断是否不为null和undefined
     */
    static isNotNull(obj) {
        if (obj != null && obj != undefined) {
            return true;
        }
        return false;
    }

    /**
     * 将值转换为boolean
     * 仅当值为1或true或"true"或"TRUE"时,返回true
     */
    static toBoolean(val) {
        return !!(this.isNotEmpty(val) && (val === 1 || val === "1" || val === true || val === "true" || val === "TRUE"));
    }

    /**
     * 数组转为字符串,形式如: "1","2","3"
     *
     */
    static arrayToString(arr, spliter = ",") {
        let str = "";
        if (this.isNotNull(arr)) {
            for (let i = 0; i < arr.length; i++) {
                str += spliter + arr[i];
            }
            if (str.length > 0) {
                str = str.substring(1, str.length);
            }
        }
        return str;
    }

    /**
     * 判断是否为JSON字符串
     * @param {String} str
     */
    static isJsonStr(str) {
        try {
            if (typeof JSON.parse(str) == "object") {
                return true;
            }
        } catch (e) {
        }
        return false;
    }


}

export default StringUtil;
