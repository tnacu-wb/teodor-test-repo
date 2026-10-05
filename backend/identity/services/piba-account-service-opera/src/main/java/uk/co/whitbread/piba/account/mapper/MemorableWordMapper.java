package uk.co.whitbread.piba.account.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.piba.account.model.ResetMemorableWordResponse;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWord;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWordResponse;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MemorableWordMapper {

    @Mapping(source = "request.sessionToken.sessionId", target = "sessionId")
    @Mapping(source = "request.sessionToken.nonce", target = "nonce")
    @Mapping(source = "request.sessionToken.timestamp", target = "timestamp")
    @Mapping(source = "request.sessionToken.hash", target = "hash")
    @Mapping(source = "request.newMemorableWord", target = "newMemorableWord")
    UpdateMemorableWordRequest toUpdateMemorableWordRequest(UpdateMemorableWord updateMemorableWord);

    @Mapping(source = "sessionId", target = "request.sessionToken.sessionId")
    @Mapping(source = "nonce", target = "request.sessionToken.nonce")
    @Mapping(source = "timestamp", target = "request.sessionToken.timestamp")
    @Mapping(source = "hash", target = "request.sessionToken.hash")
    @Mapping(source = "newMemorableWord", target = "request.newMemorableWord")
    UpdateMemorableWord toUpdateMemorableWord(UpdateMemorableWordRequest updateMemorableWord);

    @Mapping(source = "response.resultCode", target = "resultCode")
    ResetMemorableWordResponse toResetMemorableWordResponse(UpdateMemorableWordResponse updateMemorableWordResponse);

}
