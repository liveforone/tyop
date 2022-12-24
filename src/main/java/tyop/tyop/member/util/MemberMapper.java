package tyop.tyop.member.util;

import tyop.tyop.member.dto.MemberRequest;
import tyop.tyop.member.dto.MemberResponse;
import tyop.tyop.member.model.Member;

public class MemberMapper {

    public static Member dtoToEntity(MemberRequest memberRequest) {
        return Member.builder()
                .id(memberRequest.getId())
                .email(memberRequest.getEmail())
                .password(memberRequest.getPassword())
                .auth(memberRequest.getAuth())
                .blockCount(memberRequest.getBlockCount())
                .nickname(memberRequest.getNickname())
                .build();
    }

    public static MemberResponse dtoBuilder(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .auth(member.getAuth())
                .blockCount(member.getBlockCount())
                .nickname(member.getNickname())
                .build();
    }
}
