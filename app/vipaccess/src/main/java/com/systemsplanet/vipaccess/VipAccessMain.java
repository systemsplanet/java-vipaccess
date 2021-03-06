package com.systemsplanet.vipaccess;

import com.systemsplanet.vipaccess.cmd.CmdConfig;
import com.systemsplanet.vipaccess.http.VipProvision;
import com.systemsplanet.vipaccess.http.VipRequest;
import com.systemsplanet.vipaccess.http.VipToken;
import com.systemsplanet.vipaccess.log.Log;

public class VipAccessMain {
    static Log LOG = new Log();

    public static void main(String args[]) {
        try {
            CmdConfig cfg = new CmdConfig();
            cfg.init(args);
            LOG.info(cfg);

            String xmlReq = VipProvision.getRequestXml(cfg.mode);
            LOG.info("request: " + xmlReq);

            VipRequest req = new VipRequest();
            String xmlResp = req.send(xmlReq);
            LOG.info("response:" + xmlResp);

            VipToken token = new VipToken().parse(xmlResp);
            LOG.info(token);

            byte[] otp_secret = VipEnc.decrypt(VipConst.AES_KEY, token.iv, token.cipher);

            //if (req.checkCredential(token.id, otp_secret) == false) {
             //   LOG.error("Something went wrong--the token is invalid.");
           // }
            LOG.info("BE AWARE that this new credential expires on this date: " + token.expiry);
            LOG.info("E*Trade Id: " + token.id);
            new VipQrCode().writeQRCode(token, otp_secret);
            LOG.info("done");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
