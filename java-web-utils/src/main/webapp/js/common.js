/**获取随机数字符串*/
function getRandomNum(){
    return new Date().getTime()+""+Math.round(Math.random()*100);
}