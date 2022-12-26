package tyop.tyop.filteringBot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilteringBotTest {

    @Test
    void ignoreBlankCheckBadWordTest() {
        //given
        String haveBlankBadWord = "시 발놈아";

        //when
        boolean hasBadWord = FilteringBot.ignoreBlankCheckBadWord(haveBlankBadWord);

        //then
        Assertions.assertThat(hasBadWord).isTrue();
    }
}