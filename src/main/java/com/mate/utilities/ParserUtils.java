package com.mate.utilities;

import com.mate.models.responses.validation.ValidationResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

@Slf4j
public class ParserUtils {

  public static ValidationResponse queryValidator(String query) {
    String comment = "Query Parsed Successfully";
    ValidationResponse validationResponse = new ValidationResponse(false);
    try {
      query = query.replace("{", "");
      query = query.replace("}", "");
      Statement statement =
          CCJSqlParserUtil.parse(query, parser -> parser.withSquareBracketQuotation(true));
      Select selectStatement = (Select) statement;
      if (Objects.nonNull(selectStatement)) {
        validationResponse.setIsValid(true);
      }
      if (!validateQueryEnd(query)) {
        validationResponse.setIsValid(false);
        validationResponse.setComment("Query cannot end with limit || ;");
        log.info("Limit or ; validation failed for query - " + query);
      }
    } catch (JSQLParserException e) {
      String failure = e.getMessage();
      boolean bypassError = failure.contains("timestamp with time zone");
      if (bypassError) {
        validationResponse.setIsValid(true);
        validationResponse.setComment(comment);
        log.info("Query Parsed by bypassing the redshift custom functions");
      } else {
        validationResponse.setIsValid(false);
        validationResponse.setComment(e.getMessage());
        log.error(e.getMessage());
      }
    }
    if (validationResponse.getIsValid()) validationResponse.setComment(comment);
    return validationResponse;
  }

  public static boolean validateQueryEnd(String query) {
    String trimQuery = query.replaceAll("\\s+$", "");
    Pattern p1 = Pattern.compile("(LIMIT|limit) (\\d+)$");
    Pattern p2 = Pattern.compile(";$");
    return !p1.matcher(trimQuery).find() && !p2.matcher(trimQuery).find();
  }

  public static List<String> fetchColumns(String query) {
    Set<String> uniqueParams = new HashSet<>();
    try {
      Pattern p = Pattern.compile("\\{(.*?)\\}");
      Matcher m = p.matcher(query);
      while (m.find()) {
        uniqueParams.add(m.group(1));
      }
      log.info("fetching parametrised columns");
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return new ArrayList<String>(uniqueParams);
  }

  public static String addUserDetailsInMetadata(String userName, String jobName) {
    StringBuilder comments = new StringBuilder();
    try {
      comments
          .append("/*")
          .append("\n")
          .append("DataStitchQuery")
          .append("\n")
          .append("User: ")
          .append(userName)
          .append("\n")
          .append("JobName: ")
          .append(jobName)
          .append("\n")
          .append("*/")
          .append("\n");
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return comments.toString();
  }
}
