package com.baodanyun.websocket.model;

import com.baodanyun.websocket.util.XmllUtil;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.List;

public class Ofvcard {
    private String username;

    private String vcard;

    private User user;

    private boolean analysis = false;

    public Ofvcard.User getUser() {
        try {
            return analysisVcard();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User userInit = new User();
        this.user = userInit;
        return user;
    }

    public void setUser(Ofvcard.User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getVcard() {
        return vcard;
    }

    public void setVcard(String vcard) {
        this.vcard = vcard == null ? null : vcard.trim();
    }

    public synchronized User analysisVcard() throws JDOMException, IOException {
       /* vcard = "<vCard xmlns=\"vcard-temp\">\n" +
                "        <NICKNAME>萨沙</NICKNAME>\n" +
                "        <FN>微信好友[&lt;span&gt;萨沙&lt;/span&gt;]</FN>\n" +
                "        <need>true</need>\n" +
                "        <PHOTO>\n" +
                "            <BINVAL>/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDABQODxIPDRQSEBIXFRQYHjIhHhwcHj0sLiQySUBMS0dA\n" +
                "RkVQWnNiUFVtVkVGZIhlbXd7gYKBTmCNl4x9lnN+gXz/2wBDARUXFx4aHjshITt8U0ZTfHx8fHx8\n" +
                "fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHx8fHz/wAARCACEAIQDASIA\n" +
                "AhEBAxEB/8QAGwAAAQUBAQAAAAAAAAAAAAAABQACAwQGAQf/xAA2EAABBAEDAwMBBgQGAwAAAAAB\n" +
                "AAIDEQQSITEFQVETImFxBhQyQoGRIzNSoRUWU2JzsaLB4f/EABgBAQEBAQEAAAAAAAAAAAAAAAEC\n" +
                "AwAE/8QAIBEBAQEAAgMAAgMAAAAAAAAAAAERAiESMUEDURNhcf/aAAwDAQACEQMRAD8A1cf8Kg82\n" +
                "XJ7rPASc0Oq+yZJksj2uz4Ci5IZtrrWuDiSE7ardsUPyOo+mwuc4NA7rN5v2jmc9zYGhoH5nblRO\n" +
                "X6aeF+tg58TTZduPlQv6ljNdpMrNXjULXn8+bk5BPqzvcPF0P2RPoWJqDp3j4am2wzhK07+qwxka\n" +
                "pBZO2yrP+0uKxxaXmx/sKrywNlaWuH/xBuoYbjZr+I3/AMh5US1pfxwe/wAzYpP8yvq0ong9Txs9\n" +
                "v8CUOcOR3XnJHlXukyuiyhodpdy0/K03GfhvT0IE+Eg8EkAgkLG9T6/nBzGscIa5LO/7qni9dyoZ\n" +
                "NTpC++b5T5fpP8d+t45rH/jFkKtNiAjUzelR6V1qLOOh7g2Tte1ok+XTKxjSLPItdbBllxUoM2Ir\n" +
                "6hcMkYH4wiTmtkbTxYVCfFihdYFg+d0XiZy1CZov9QJJ2lg4Df2SQrU0uQ4At1fVU3yAWTwBdp0s\n" +
                "T4zRuwbryqryRRA1ACq+F57bb2348ZJ0H9VmLoNOrcPFjyK2QFwso51BodC2qJaavg0hEcRkcABy\n" +
                "tuF6HKGwRGWVrLrUdz4WxxceOOFrIiC1orZZ+CfExOWmR3BdWyN9NyIchuuEgVyE8u0zpb0BUc+R\n" +
                "mnQ1mp/auyIyn27IbmTNiY1kWn1nmgXcD5KmRW9AmRgzEOk0D6BV4dUGUzVsQQpTPmmYgyOvnYWF\n" +
                "2SKWRollbTwrqZ2f1toGlw5O6FsieQOK7bon1MmbHx3DmqKp4uU3GmdqZrBXT07lnl2ljwsk06Mi\n" +
                "xxRWg6HHkRytdlWK3G92mYLoc2MOiqxyO4RWGIj2u5G7So2qsmCLZo3DZwVTMlY8t0OsjnwuCIGy\n" +
                "BV8hUTE8HcHZX5MfGfEuvyUkz03+EkacW55vVNForsqzoWv+Cm+pf5TXlMMrroNU2Sqm8fSv1PEf\n" +
                "HilxogEboV02Avnc0jaijefJJJgSMd2Fqh0hw9cg/mXemnG+U7TM6ODEI3P9gJIGkbWp8PpzMWQO\n" +
                "YXbCtyiGmgu/Vdqcwx+7FXbEC6yASrbh7VCDTh4JXOjhjB/KP2UUkDHMLXDYhXC2hagkNBFVGayo\n" +
                "HtHonhpJCDSAiQgijaPdSzWwZYBbq2/ZA8h/qyukrSHHgdlpx1n+SiHQsp0GcxvLZPaR/wBLbNF0\n" +
                "bohYDpQvqOP/AMgXoDRYC7lBxvR4kHjdRvdtYHPZdOyjre7UV2IzJJfCSfqA/MkoUb6Y8LrIwDun\n" +
                "SawAWgLoBcAbAVJ1Uz3N+5y2PyndZnGyfu87XdrWk6yGxYD7O7tgFkXjVGfNqpNXxuRtsbJZNECD\n" +
                "ypOd1kul9QMLxHJwdloo3uedcb9q4KL0rq+lsuOk2Baia08k2oy7IPLWqMxzOPufQ+FzsWmyhxLA\n" +
                "QSOyr5LwxjnONBotPiY2EEj9UG6tl62PYzgco+uAMmc5E8kh7nb6JmmwmtDR35UzKI4W3pjO03TB\n" +
                "p6ljf8gW+bwvPYX+nlRPO2l4P916DGbaFNJOXKbW4sp7m7WodTjyFFKT2f0pJtOSQMd/SkhV7poc\n" +
                "bop4PGyQz/2me8Px2i9O5QTg/BR77RRudIySrZp0kLPO2vu3/pVFw6MBkzSVYwupyYcxaffDZJb4\n" +
                "+QqmtvDr+Co3EA8f3T/rr/Td40rMiFskTg5rhsQn6a5WM6XnTYj6ieQ08tO4R7/EZpW76R8hRZip\n" +
                "LUvUJyxhaw+4oLmCoa8oi/3GzuqOYy6HZE9rzoHMQc11e0t3KUZs0rDxoa+hvSrQOsuBFLX2wzK6\n" +
                "4Wd1tuiZP3nBZqNvZ7SsS40VofsvK8TSRO4c2/2RS1AOyicPcQpWpPaSLHKmxKH3JLh1DkG/hJTh\n" +
                "NLXuPNJ+jSBbl23Ac2UtJq3bKpAgzcRmTAWO+oI8rGTMLS7bS4GiFs3y7EEUOyzvVWNLy9rQHHn5\n" +
                "TmK40FB3LXCkyqeGk34UsjNX4hTvKbCCZmXex3VCpoG+7YUi0H4Qnu6VpIkhcHRHuO3wrEeOWqOT\n" +
                "XjejaJChmj1EfRXPTrZL0ddAAkox2g+TiO+4yzNB9rq/RCWN7g91qepRMbEMZoB7uP8A6WcdEInO\n" +
                "aOxWkvxnm3SppNcd0U6G70uoR3wdrQk213P6Kxi5HpzRvdtpcD/dCm/ant3UcZBaCpG8rmVNojkp\n" +
                "KSgeyS4arMbW5ScdlwE0UyQ0LRFKGVMGNfZGyA5Uxe8lu4VzqQkktwIDSdkLIddOdR+i5pxnSMkm\n" +
                "9v0XYXMD7OyT2vb4cExlGUfKSNYPUWY1AOuP+khGcV+LlvqJ29WRaBYB0zek4WCLCKRwRslFjSXc\n" +
                "OGxBRKnlF92DZ/Ft3ViKBkUemrPc+VUGW8M0afUlB0ntfz+yeyeXSS9nfajarqMr5UOy46lkJ772\n" +
                "s5Iy5XO+VqchrpNbnW0HakDyY/TJsivkKK34eg0tGok9uVPHjiRgcBVHhRkRh3ttEMUa2aaITpxq\n" +
                "cJ+vFid5aFZB3VHpr9WOAa9u2yvBLGpAkmWUkoxARQUcg1NoqVxUb/hFi4EzwCWB0ThTgKtCXQFn\n" +
                "skG48rUshje/mnHkdiq/UMYSRk6BrG9tUVpx5d4yjiWOLXgihe/hVpSCfUYdxyiHUQXxtbptw2v4\n" +
                "Q+LGkv8ACSFU9abu4JYby+eJw5Wha0OYC7gDuhHSscMp8xaNtgUUePXcImH28ml2iw/EH8yQDZ7t\n" +
                "rVnVQTfScxoDHUB2IUYe66eP2SiuSOv5VHLxBMw6tkRaNTia27JssRIKDGamxDAPa0u+eV3EhnY9\n" +
                "z/TJI23dQCPDE1s7klPZi+mO30RIu8jejOlqQTMa3gijdosFQxBpnPkhXwUs66kuWklKN34lHJyf\n" +
                "okklxsX8yvIVjSKqkklBrL9RjaZZBW2oocwmyLIodkkkPRPSbGuSUNc4kWtBgtDCQPASSSnmvdlC\n" +
                "eUklTF3uphG2rO6SS6OOocAUPhRvFJJKqFeI1lM+bRAJJKFV1JJJIf/Z</BINVAL>\n" +
                "            <TYPE>image/jpeg</TYPE>\n" +
                "        </PHOTO>\n" +
                "    </vCard>";*/
        if (!analysis) {
            analysis = true;
        } else {
            return user;
        }
        User userInit = new User();
        Element el = XmllUtil.xmlElementRoot(vcard);
        if (null != el) {
            //System.out.println(el.getName());
            List<Element> list = el.getChildren();
            if (null != list) {
                for (Element c1 : list) {
                    String name = c1.getName();
                    String text = c1.getText();
                    //System.out.println("c1"+name);
                    //System.out.println("c1"+text);

                    if ("NICKNAME".equals(name)) {
                        userInit.setNickName(text);
                    } else if ("FN".equals(name)) {
                        userInit.setFn(text);
                    } else if ("PHOTO".equals(name)) {
                        List<Element> c1s = c1.getChildren();
                        if (null != c1s) {
                            for (Element c2 : c1s) {
                                String name2 = c2.getName();
                                String text2 = c2.getText();

                                //System.out.println("c2"+name2);
                                //System.out.println("c2"+text2);
                                if ("BINVAL".equals(name2)) {
                                    if (StringUtils.isNotEmpty(text2)) {
                                        userInit.setAvatar(text2);
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }

        this.user = userInit;
        return user;
    }

    public class User {
        private String nickName;
        private String fn;
        private String avatar;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getFn() {
            return fn;
        }

        public void setFn(String fn) {
            this.fn = fn;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }


}