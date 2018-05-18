/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.jaydenxiao.androidfire.api;

public class ApiConstants {


    public static String IP="10.2.72.163"                                                                                                                                                                                                                                                                                                                         ;
    public static String PORT="8889";
    //public static final String WORK_CHECK="http://10.71.16.236:8889/";
    /*public static String IP="172.29.39.15";
    public static String PORT="8889";*/
    public static String WORK_CHECK="http://"+IP+":"+PORT+"/";
    public static final int inSampleSize=3;

    /**4，
     *
     * @param hostType host类型
     * @return host
     */
    public static String getHost(int hostType) {
        String host;
        switch (hostType) {
            case HostType.WORK_CHECK:
                host = WORK_CHECK;
                break;
            default:
                host=WORK_CHECK;
                break;
        }
        return host;
    }
}
