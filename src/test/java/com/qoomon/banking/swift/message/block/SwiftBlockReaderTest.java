package com.qoomon.banking.swift.message.block;

import com.qoomon.banking.TestUtils;
import com.qoomon.banking.swift.message.block.exception.BlockParseException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 26/07/16.
 */
public class SwiftBlockReaderTest {

    @Test
    public void readBlock_SHOULD_read_valid_blocks() throws Exception {
        // Given

        String blockText = "{1:a}{2:b}{3:c}";

        SwiftBlockReader subjectUnderTest = new SwiftBlockReader(new StringReader(blockText));

        // When
        List<GeneralBlock> blockList = TestUtils.collectUntilNull(subjectUnderTest::readBlock);

        // Then

        assertThat(blockList).hasSize(3);
        assertThat(blockList.get(0).getId()).isEqualTo("1");
        assertThat(blockList.get(0).getContent()).isEqualTo("a");
        assertThat(blockList.get(1).getId()).isEqualTo("2");
        assertThat(blockList.get(1).getContent()).isEqualTo("b");
        assertThat(blockList.get(2).getId()).isEqualTo("3");
        assertThat(blockList.get(2).getContent()).isEqualTo("c");

    }

    @Test
    public void readBlock_WHEN_unfinished_block_detected_THROW_exception() throws Exception {
        // Given

        String blockText = "{1:a}{2:b}{3:c";

        SwiftBlockReader subjectUnderTest = new SwiftBlockReader(new StringReader(blockText));

        // When
        Throwable exception = catchThrowable(() -> TestUtils.collectUntilNull(subjectUnderTest::readBlock));

        // Then
        assertThat(exception).isInstanceOf(BlockParseException.class);
    }

    @Test
    public void readBlock_SHOULD_handle_CRLF_line_endings() throws Exception {
        // Given

        String blockText = "{1:a}{2:b}{3:c}{4:\r\n-}";

        SwiftBlockReader subjectUnderTest = new SwiftBlockReader(new StringReader(blockText));

        // When
        List<GeneralBlock> blockList = TestUtils.collectUntilNull(subjectUnderTest::readBlock);

        // Then

        assertThat(blockList).hasSize(4);
        assertThat(blockList.get(0).getId()).isEqualTo("1");
        assertThat(blockList.get(0).getContent()).isEqualTo("a");
        assertThat(blockList.get(1).getId()).isEqualTo("2");
        assertThat(blockList.get(1).getContent()).isEqualTo("b");
        assertThat(blockList.get(2).getId()).isEqualTo("3");
        assertThat(blockList.get(2).getContent()).isEqualTo("c");
        assertThat(blockList.get(3).getId()).isEqualTo("4");
        assertThat(blockList.get(3).getContent()).isEqualTo("\n-");
    }

    @Test
    public void readBlock_SHOULD_handle_LF_line_endings() throws Exception {
        // Given

        String blockText = "{1:a}{2:b}{3:c}{4:\n-}";

        SwiftBlockReader subjectUnderTest = new SwiftBlockReader(new StringReader(blockText));

        // When
        List<GeneralBlock> blockList = TestUtils.collectUntilNull(subjectUnderTest::readBlock);

        // Then

        assertThat(blockList).hasSize(4);
        assertThat(blockList.get(0).getId()).isEqualTo("1");
        assertThat(blockList.get(0).getContent()).isEqualTo("a");
        assertThat(blockList.get(1).getId()).isEqualTo("2");
        assertThat(blockList.get(1).getContent()).isEqualTo("b");
        assertThat(blockList.get(2).getId()).isEqualTo("3");
        assertThat(blockList.get(2).getContent()).isEqualTo("c");
        assertThat(blockList.get(3).getId()).isEqualTo("4");
        assertThat(blockList.get(3).getContent()).isEqualTo("\n-");
    }

}