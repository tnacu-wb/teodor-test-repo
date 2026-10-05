package uk.co.whitbread.business.tether.converter;

import uk.co.whitbread.business.tether.model.PibaTetheredGuidRequest;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumber;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumber;

public interface WorldlineTransformer {
    TetherByAccountNumber toTetherByAccountRequest(TetherLinkRequest tetherLinkRequest, Scheme scheme);

    TetherByCardNumber toTetherByCardRequest(TetherLinkRequest tetherLinkRequest, Scheme scheme);

    LoginTetheredUser toLoginTetheredUserRequest(String guid, Scheme scheme);

    PibaTetheredGuidRequest toPibaTetheredGuidRequest(String tetherGuid, EmployeeDetails employeeDetails,
                                                      Scheme scheme);
}
