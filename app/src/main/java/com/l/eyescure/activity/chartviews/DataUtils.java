package com.l.eyescure.activity.chartviews;

import java.util.ArrayList;

public class DataUtils {

    public static DataBean getDatas(int model) {
        ArrayList<Integer> xList = new ArrayList<>();
        ArrayList<Double> yList = new ArrayList<>();
        DataBean dataBean = new DataBean();
        if (model == 1) {
            for (int i = 0; i < 210; i++) {
                xList.add(i);
                //0-2
                if (i < 30) {
                    if (i == 15) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }

                if (i >= 30 && i < 33) {
                    yList.add(0.0);
                }
                if (i >= 33 && i < 36) {
                    yList.add(0.4 / 7 * 100);
                }
                if (i >= 36 && i < 39) {
                    yList.add(0.8 / 7 * 100);
                }
                if (i >= 39 && i < 42) {
                    yList.add(1.2 / 7 * 100);
                }
                if (i >= 42 && i < 45) {
                    yList.add(1.6 / 7 * 100);
                }
                if (i >= 45 && i < 48) {
                    yList.add(2.0 / 7 * 100);
                }
                if (i >= 48 && i < 51) {
                    yList.add(2.4 / 7 * 100);
                }
                if (i >= 51 && i < 54) {
                    yList.add(2.8 / 7 * 100);
                }
                if (i >= 54 && i < 57) {
                    yList.add(3.2 / 7 * 100);
                }
                if (i >= 57 && i < 60) {
                    yList.add(3.5 / 7 * 100);
                }

                //4-5
                if (i >= 60 && i < 75) {
                    if (i % 2 != 0) {
                        yList.add(0.0);
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }


                //这个是新增的表  5-6
                if (i >= 75 && i < 90) {
                    if (i == 75) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }

                //6-7
                if (i >= 90 && i < 105) {
                    if (i % 2 != 0) {
                        yList.add(5.3 / 7 * 100);
                    } else {
                        {
                            yList.add(0.0);
                        }
                    }
                }

                // 7-9
                if (i >= 105 && i < 135) {

                    if (i == 120) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }

                //9-11  爬坡
                if (i >= 135 && i < 138) {
                    yList.add(0.0);
                }
                if (i >= 138 && i < 141) {
                    yList.add(0.4 / 7 * 100);
                }
                if (i >= 141 && i < 144) {
                    yList.add(0.8 / 7 * 100);
                }
                if (i >= 144 && i < 147) {
                    yList.add(1.2 / 7 * 100);
                }
                if (i >= 147 && i < 150) {
                    yList.add(1.6 / 7 * 100);
                }
                if (i >= 150 && i < 153) {
                    yList.add(2.0 / 7 * 100);
                }
                if (i >= 153 && i < 156) {
                    yList.add(2.4 / 7 * 100);
                }
                if (i >= 156 && i < 159) {
                    yList.add(2.8 / 7 * 100);
                }
                if (i >= 159 && i < 162) {
                    yList.add(3.2 / 7 * 100);
                }
                if (i >= 162 && i < 165) {
                    yList.add(3.5 / 7 * 100);
                }

                // 11-12
                if (i >= 165 && i < 180) {
                    if (i % 2 == 0) {
                        {
                            yList.add(0.0);
                        }
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }

                //12-13
                if (i >= 180 && i < 195) {
                    if (i==180) yList.add(0.0);
                    else
                    yList.add(3.5 / 7 * 100);
                }

                if (i >= 195) {
                    if (i % 2 != 0) {
                         yList.add(0.0);

                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }
            }
        } else if (model == 2) {
            for (int i = 0; i < 240; i++) {
                xList.add(i);
                //0-2
                if (i < 30) {
                    if (i == 15) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }


                //2-4爬坡
                if (i >= 30 && i < 33) {
                    yList.add(0.0);
                }
                if (i >= 33 && i < 36) {
                    yList.add(0.4 / 7 * 100);
                }
                if (i >= 36 && i < 39) {
                    yList.add(0.8 / 7 * 100);
                }
                if (i >= 39 && i < 42) {
                    yList.add(1.2 / 7 * 100);
                }
                if (i >= 42 && i < 45) {
                    yList.add(1.6 / 7 * 100);
                }
                if (i >= 45 && i < 48) {
                    yList.add(2.0 / 7 * 100);
                }
                if (i >= 48 && i < 51) {
                    yList.add(2.4 / 7 * 100);
                }
                if (i >= 51 && i < 54) {
                    yList.add(2.8 / 7 * 100);
                }
                if (i >= 54 && i < 57) {
                    yList.add(3.2 / 7 * 100);
                }
                if (i >= 57 && i < 60) {
                    yList.add(3.5 / 7 * 100);
                }

                //4-6
                if (i >= 60 && i < 90) {
                    if (i % 2 != 0) {
                        yList.add(0.0);
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }

                // 6-7
                if (i >= 90 && i < 105) {
                    yList.add(3.5 / 7 * 100);
                }

                //7-8
                if (i >= 105 && i < 120) {
                    if (i % 2 != 0) {
                        yList.add(0.0);
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }

                //8-10
                if (i >= 120 && i < 150) {
                    if (i == 135) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }

                //10-12 爬坡
                if (i >= 150 && i < 153) {
                    yList.add(0.0);
                }
                if (i >= 153 && i < 156) {
                    yList.add(0.4 / 7 * 100);
                }
                if (i >= 156 && i < 159) {
                    yList.add(0.8 / 7 * 100);
                }
                if (i >= 159 && i < 162) {
                    yList.add(1.2 / 7 * 100);
                }
                if (i >= 162 && i < 165) {
                    yList.add(1.6 / 7 * 100);
                }
                if (i >= 165 && i < 168) {
                    yList.add(2.0 / 7 * 100);
                }
                if (i >= 168 && i < 171) {
                    yList.add(2.4 / 7 * 100);
                }
                if (i >= 171 && i < 174) {
                    yList.add(2.8 / 7 * 100);
                }
                if (i >= 174 && i < 177) {
                    yList.add(3.2 / 7 * 100);
                }
                if (i >= 177 && i < 180) {
                    yList.add(3.5 / 7 * 100);
                }

                //12-14

                if (i >= 180 && i < 210) {
                    if (i % 2 != 0) {
                        yList.add(0.0);
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }


                //14-15
                if (i >= 210 && i < 225) {
                    yList.add(3.5 / 7 * 100);
                }

                //15-16
                if (i >= 225) {
                    if (i % 2 != 0) {
                        if (i == 239) {
                            yList.add(0.3 / 7 * 100);
                     } else
                        yList.add(0.0);
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }
            }
        } else if (model == 3) {
            for (int i = 0; i < 240; i++) {
                xList.add(i);
                //0-2
                if (i < 30) {
                    if (i == 15) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }

                //2-4爬坡
                if (i >= 30 && i < 33) {
                    yList.add(0.0);
                }
                if (i >= 33 && i < 36) {
                    yList.add(0.4 / 7 * 100);
                }
                if (i >= 36 && i < 39) {
                    yList.add(0.8 / 7 * 100);
                }
                if (i >= 39 && i < 42) {
                    yList.add(1.2 / 7 * 100);
                }
                if (i >= 42 && i < 45) {
                    yList.add(1.6 / 7 * 100);
                }
                if (i >= 45 && i < 48) {
                    yList.add(2.0 / 7 * 100);
                }
                if (i >= 48 && i < 51) {
                    yList.add(2.4 / 7 * 100);
                }
                if (i >= 51 && i < 54) {
                    yList.add(2.8 / 7 * 100);
                }
                if (i >= 54 && i < 57) {
                    yList.add(3.2 / 7 * 100);
                }
                if (i >= 57 && i < 60) {
                    yList.add(3.5 / 7 * 100);
                }

                //4-5
                if (i >= 60 && i < 75) {
                    if (i % 2 != 0) {
                        yList.add(0.0);
                    } else {
                            yList.add(5.3 / 7 * 100);
                        }

                    }


                // 5-7
                if (i >= 75 && i < 105) {
                    if (i==75||i == 90) {
                        yList.add(0.0); //0.4/7*100);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }


                //7-8
                if (i >= 105 && i < 120) {
                    if (i % 2 != 0) {
                        yList.add(0.0);
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }

                //8-10
                if (i >= 120 && i < 150) {
                    if (i == 135) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }

                //10-12 爬坡
                if (i >= 150 && i < 153) {
                    yList.add(0.0);
                }
                if (i >= 153 && i < 156) {
                    yList.add(0.4 / 7 * 100);
                }
                if (i >= 156 && i < 159) {
                    yList.add(0.8 / 7 * 100);
                }
                if (i >= 159 && i < 162) {
                    yList.add(1.2 / 7 * 100);
                }
                if (i >= 162 && i < 165) {
                    yList.add(1.6 / 7 * 100);
                }
                if (i >= 165 && i < 168) {
                    yList.add(2.0 / 7 * 100);
                }
                if (i >= 168 && i < 171) {
                    yList.add(2.4 / 7 * 100);
                }
                if (i >= 171 && i < 174) {
                    yList.add(2.8 / 7 * 100);
                }
                if (i >= 174 && i < 177) {
                    yList.add(3.2 / 7 * 100);
                }
                if (i >= 177 && i < 180) {
                    yList.add(3.5 / 7 * 100);
                }

                //12-13

                if (i >= 180 && i <= 195) {
                    if (i % 2 != 0) {
                        yList.add(0.0);
                    } else {
                        yList.add(5.3 / 7 * 100);
                    }
                }


                //13-15
                if (i > 195 && i < 225) {
                    if (i == 210) {
                        yList.add(0.0);
                    } else {
                        yList.add(3.5 / 7 * 100);
                    }
                }


                //15-16
                if (i >= 225) {
                    if (i % 2 != 0) {
                        if (i == 239) {
                            yList.add(0.3 / 7 * 100);
                        } else {
                            yList.add(0.0);
                        }
                    } else
                       {
                        yList.add(5.3 / 7 * 100);
                    }
                }

            }
        }

        dataBean.setxList(xList);
        dataBean.setyList(yList);
        return dataBean;

    }
}
