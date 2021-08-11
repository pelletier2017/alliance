/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.transformer.nitf.image;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.alliance.transformer.nitf.NitfAttributeConverters;
import org.codice.alliance.transformer.nitf.common.NitfAttribute;
import org.codice.imaging.nitf.core.text.TextSegment;

/** NitfAttributes to represent the properties of a TextSegment. */
public enum TextAttribute implements NitfAttribute<TextSegment> {

  /*
   * Non-normalized attributes
   */

  FILE_PART_TYPE("file-part-type", "TE", segment -> "TE"),
  TEXT_IDENTIFIER("text-identifier", "TEXTID", TextSegment::getIdentifier),
  TEXT_ATTACHMENT_LEVEL(
      "text-attachment-level",
      "TXTALVL",
      TextSegment::getAttachmentLevel,
      Collections.singletonList(BasicTypes.INTEGER_TYPE)),
  TEXT_DATE_AND_TIME(
      "text-date-and-time",
      "TXTDT",
      segment -> NitfAttributeConverters.nitfDate(segment.getTextDateTime()),
      Collections.singletonList(BasicTypes.DATE_TYPE)),
  TEXT_TITLE("text-title", "TXTITL", TextSegment::getTextTitle),
  TEXT_SECURITY_CLASSIFICATION(
      "text-security-classification",
      "TSCLAS",
      segment -> segment.getSecurityMetadata().getSecurityClassificationSystem()),
  TEXT_CLASSIFICATION_SECURITY_SYSTEM(
      "text-classification-security-system",
      "TSCLSY",
      segment -> segment.getSecurityMetadata().getSecurityClassificationSystem()),
  TEXT_CODEWORDS(
      "text-codewords", "TSCODE", segment -> segment.getSecurityMetadata().getCodewords()),
  TEXT_CONTROL_AND_HANDLING(
      "text-control-and-handling",
      "TSCTLH",
      segment -> segment.getSecurityMetadata().getControlAndHandling()),
  TEXT_RELEASING_INSTRUCTIONS(
      "text-releasing-instructions",
      "TSREL",
      segment -> segment.getSecurityMetadata().getReleaseInstructions()),
  TEXT_DECLASSIFICATION_TYPE(
      "text-declassification-type",
      "TSDCTP",
      segment -> segment.getSecurityMetadata().getDeclassificationType()),
  TEXT_DECLASSIFICATION_DATE(
      "text-declassification-date",
      "TSDCDT",
      segment -> segment.getSecurityMetadata().getDeclassificationDate()),
  TEXT_DECLASSIFICATION_EXEMPTION(
      "text-declassification-exemption",
      "TSDCXM",
      segment -> segment.getSecurityMetadata().getDeclassificationExemption()),
  TEXT_DOWNGRADE("text-downgrade", "TSDG", segment -> segment.getSecurityMetadata().getDowngrade()),
  TEXT_DOWNGRADE_DATE(
      "text-downgrade-date", "TSDGDT", segment -> segment.getSecurityMetadata().getDowngradeDate()),
  TEXT_CLASSIFICATION_TEXT(
      "text-classification-text",
      "TSCLTX",
      segment -> segment.getSecurityMetadata().getClassificationText()),
  TEXT_CLASSIFICATION_AUTHORITY_TYPE(
      "text-classification-authority-type",
      "TSCATP",
      segment -> segment.getSecurityMetadata().getClassificationAuthorityType()),
  TEXT_CLASSIFICATION_AUTHORITY(
      "text-classification-authority",
      "TSCAUT",
      segment -> segment.getSecurityMetadata().getClassificationAuthority()),
  TEXT_CLASSIFICATION_REASON(
      "text-classification-reason",
      "TSCRSN",
      segment -> segment.getSecurityMetadata().getClassificationReason()),
  TEXT_SECURITY_SOURCE_DATE(
      "text-security-source-date",
      "TSSRDT",
      segment -> segment.getSecurityMetadata().getSecuritySourceDate()),
  TEXT_SECURITY_CONTROL_NUMBER(
      "text-security-control-number",
      "TSCTLN",
      segment -> segment.getSecurityMetadata().getSecurityControlNumber()),
  TEXT_FORMAT("text-format", "TXTFMT", segment -> segment.getTextFormat().name()),
  TEXT_EXTENDED_SUBHEADER_DATA_LENGTH(
      "text-extended-subheader-data-length",
      "TXSHDL",
      TextSegment::getExtendedHeaderDataOverflow,
      Collections.singletonList(BasicTypes.INTEGER_TYPE));

  private static final String ATTRIBUTE_NAME_PREFIX = "text.";

  private final String shortName;

  private final String longName;

  private final Function<TextSegment, Serializable> accessorFunction;

  private final Set<AttributeDescriptor> attributeDescriptors;

  TextAttribute(
      final String lName, final String sName, final Function<TextSegment, Serializable> accessor) {
    this(lName, sName, accessor, Collections.singletonList(BasicTypes.STRING_TYPE));
  }

  TextAttribute(
      final String lName,
      final String sName,
      final Function<TextSegment, Serializable> accessor,
      List<AttributeType> attributeTypes) {
    this.longName = lName;
    this.shortName = sName;
    this.accessorFunction = accessor;
    this.attributeDescriptors = createAttributeDescriptors(attributeTypes);
    for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
      System.out.println(String.format("%s,%s,%s,%s", this.getClass().getSimpleName(), attributeDescriptor.getName(), shortName, longName));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String getShortName() {
    return this.shortName;
  }

  /** {@inheritDoc} */
  @Override
  public final String getLongName() {
    return this.longName;
  }

  /** {@inheritDoc} */
  @Override
  public Function<TextSegment, Serializable> getAccessorFunction() {
    return accessorFunction;
  }

  /** {@inheritDoc} */
  @Override
  public Function<TextSegment, Serializable> getExtAccessorFunction() {
    return accessorFunction;
  }

  /**
   * Equivalent to getLongName()
   *
   * @return the attribute's long name.
   */
  @Override
  public String toString() {
    return getLongName();
  }

  /** {@inheritDoc} */
  @Override
  public Set<AttributeDescriptor> getAttributeDescriptors() {
    return this.attributeDescriptors;
  }

  private Set<AttributeDescriptor> createAttributeDescriptors(List<AttributeType> attributeTypes) {
    Set<AttributeDescriptor> attributesDescriptors = new HashSet<>();
    for (AttributeType attribute : attributeTypes) {
      attributesDescriptors.add(createAttributeDescriptor(attribute));
    }
    return attributesDescriptors;
  }

  private AttributeDescriptor createAttributeDescriptor(AttributeType attributeType) {
    return new AttributeDescriptorImpl(
        ExtNitfUtility.EXT_NITF_PREFIX + ATTRIBUTE_NAME_PREFIX + longName,
        true, /* indexed */
        true, /* stored */
        false, /* tokenized */
        true, /* multivalued */
        attributeType);
  }
}
