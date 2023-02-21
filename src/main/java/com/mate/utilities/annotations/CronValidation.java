package com.mate.utilities.annotations;

import static com.mate.constants.Constants.onceCron;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.scheduling.support.CronExpression;

public class CronValidation implements ConstraintValidator<IsValidCron, String> {
  public boolean isValid(String cronExpression, ConstraintValidatorContext cxt) {
    if (!cronExpression.equals(onceCron)) {
      try {
        // Added "0 ".concat(cronExpression); because the package CronExpression validates a 6 field
        // cron the
        // initial field is used for seconds that we do not use
        cronExpression = "0 ".concat(cronExpression);
        cxt.disableDefaultConstraintViolation();
        CronExpression.parse(cronExpression);
      } catch (Exception ex) {
        // The error message will have 1 extra count of field :( TODO need to find a better way to
        // handle this
        cxt.buildConstraintViolationWithTemplate(ex.getMessage().replace("6 fields", "5 fields"))
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }
}
