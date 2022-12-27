package tyop.tyop.member.util;

import tyop.tyop.member.dto.MemberRequest;
import tyop.tyop.member.dto.MemberResponse;
import tyop.tyop.member.model.Member;
import tyop.tyop.utility.CommonUtils;

public class MemberMapper {

    public static Member dtoToEntity(MemberRequest memberRequest) {
        return Member.builder()
                .id(memberRequest.getId())
                .email(memberRequest.getEmail())
                .password(memberRequest.getPassword())
                .auth(memberRequest.getAuth())
                .blockCount(memberRequest.getBlockCount())
                .nickname(memberRequest.getNickname())
                .introduction(memberRequest.getIntroduction())
                .build();
    }

    public static MemberResponse dtoBuilder(Member member) {
        if (CommonUtils.isNull(member.getIntroduction())) {
            return MemberResponse.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .auth(member.getAuth())
                    .blockCount(member.getBlockCount())
                    .nickname(member.getNickname())
                    .introduction("한줄소개가 없습니다.")
                    .build();
        }
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .auth(member.getAuth())
                .blockCount(member.getBlockCount())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .build();
    }
}
