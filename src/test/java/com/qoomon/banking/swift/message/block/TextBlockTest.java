package com.qoomon.banking.swift.message.block;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.qoomon.banking.swift.message.submessage.field.GeneralField;
import com.qoomon.banking.swift.message.submessage.mt940.SwiftMT940;
import org.junit.Test;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by qoomon on 07/07/16.
 */
public class TextBlockTest {

    @Test
    public void of_SHOULD_remove_leading_blank_line_and_trailing_minus() throws Exception {

        // Given
        GeneralBlock generalBlock = new GeneralBlock("4","\nabc\n-");

        // When
        TextBlock textBlock = TextBlock.of(generalBlock);

        // Then
        assertThat(textBlock).isNotNull();
        assertThat(textBlock.getContent()).isEqualTo("abc\n-");

    }

}