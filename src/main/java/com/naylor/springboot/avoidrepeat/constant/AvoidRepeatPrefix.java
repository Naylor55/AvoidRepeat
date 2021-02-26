package com.naylor.springboot.avoidrepeat.constant;




public enum AvoidRepeatPrefix{

    // avoidrepeat

    Avoid_Repeat_User("AvoidRepeat.User.", "测试")
    ;

    private String prefix;
    private String describe;


    AvoidRepeatPrefix(String prefix, String describe) {
        this.prefix = prefix;
        this.describe = describe;
    }


    public String getPrefix(){
        return prefix;
    }

    public void setPrefix(String prefix){
        this.prefix=prefix;
    }

    public String getDescribe(){
        return describe;
    }

    public void setDescribe(String describe){
        this.describe=describe;
    }


}


