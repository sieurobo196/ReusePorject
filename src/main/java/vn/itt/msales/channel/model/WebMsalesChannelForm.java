package vn.itt.msales.channel.model;

/**
 *
 * @author ChinhNQ
 */
public class WebMsalesChannelForm {

    private int level = 1;
    private int channel_01;
    private int channel_02;
    private int channel_03;
    private int channel_04;
    private int channel_05;
    private int channel_06;
    private int channel_07;

    public WebMsalesChannelForm() {
    }

    public WebMsalesChannelForm(int channel_01, int channel_02, int channel_03, int channel_04, int channel_05, int channel_06, int channel_07) {
        this.channel_01 = channel_01;
        this.channel_02 = channel_02;
        this.channel_03 = channel_03;
        this.channel_04 = channel_04;
        this.channel_05 = channel_05;
        this.channel_06 = channel_06;
        this.channel_07 = channel_07;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getChannel_01() {
        return channel_01;
    }

    public void setChannel_01(int channel_01) {
        this.channel_01 = channel_01;
    }

    public int getChannel_02() {
        return channel_02;
    }

    public void setChannel_02(int channel_02) {
        this.channel_02 = channel_02;
    }

    public int getChannel_03() {
        return channel_03;
    }

    public void setChannel_03(int channel_03) {
        this.channel_03 = channel_03;
    }

    public int getChannel_04() {
        return channel_04;
    }

    public void setChannel_04(int channel_04) {
        this.channel_04 = channel_04;
    }

    public int getChannel_05() {
        return channel_05;
    }

    public void setChannel_05(int channel_05) {
        this.channel_05 = channel_05;
    }

    public int getChannel_06() {
        return channel_06;
    }

    public void setChannel_06(int channel_06) {
        this.channel_06 = channel_06;
    }

    public int getChannel_07() {
        return channel_07;
    }

    public void setChannel_07(int channel_07) {
        this.channel_07 = channel_07;
    }


}
