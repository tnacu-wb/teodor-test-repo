package uk.co.whitbread.ohip.infrastructure.rest.client.utils;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.apache.commons.lang3.tuple.Pair;

public class DateUtils {

  private DateUtils() {
  }

  /**
   * Splits the date range between the start date and end date into intervals of a specified number
   * of days, avoiding the generation of a final interval where the start and end dates are the
   * same.
   *
   * @param firstDate  start date
   * @param secondDate end date
   * @param nrDays     the number of days from an interval
   * @param dateFormat the format of the date
   * @return list of (start date, end date) pairs
   */
  public static List<Pair<LocalDate, LocalDate>> splitDateRange(String firstDate,
      String secondDate, int nrDays, String dateFormat) {
    ArrayList<Pair<LocalDate, LocalDate>> pairs = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
    var startDate = LocalDate.parse(firstDate, formatter);
    var endDate = LocalDate.parse(secondDate, formatter);
    long totalDays = 1 + DAYS.between(startDate, endDate);
    long numChunks = totalDays / nrDays;
    long lastDays = totalDays % nrDays;

    if (numChunks == 0 || nrDays == 1 || startDate.equals(endDate)) {
      pairs.add(Pair.of(startDate, endDate));
      return pairs;
    }

    if (lastDays == 1) {
      if (numChunks > 1) {
        numChunks = numChunks - 1;
      } else if (nrDays > 2) {
        nrDays = nrDays - 1;
      }
    }

    var list = getPairList(nrDays, numChunks, startDate, totalDays);
    pairs.addAll(list);

    updatePairs(nrDays, list, lastDays, endDate, pairs);
    return pairs;
  }


  /**
   * Split the date range into chunks of up to nrDays days each.
   *
   * @param nrDays    days number
   * @param numChunks chunks number
   * @param startDate start date
   * @param totalDays end date
   * @return list of (start date, end date) pairs
   */
  private static List<Pair<LocalDate, LocalDate>> getPairList(long nrDays, long numChunks,
      LocalDate startDate, long totalDays) {
    return LongStream.range(0, numChunks)
        .mapToObj(i -> {
          LocalDate chunkStartDate = startDate.plusDays(i * nrDays);
          LocalDate chunkEndDate = chunkStartDate.plusDays(
              Math.min(nrDays - 1, totalDays - i * nrDays - 1));
          return Pair.of(chunkStartDate, chunkEndDate);
        }).toList();
  }

  private static void updatePairs(int nrDays, List<Pair<LocalDate, LocalDate>> list, long lastDays,
      LocalDate endDate, ArrayList<Pair<LocalDate, LocalDate>> pairs) {
    if (!list.isEmpty() && lastDays > 0) {
      long daysNumber = 1 + DAYS.between(list.get(list.size() - 1).getRight().plusDays(1), endDate);
      var date = list.get(list.size() - 1).getRight().plusDays(1);
      if (daysNumber <= nrDays) {
        pairs.add(Pair.of(date, endDate));
      } else {
        int daysN = nrDays - 1;
        if (daysN == 1) {
          daysN++;
        }
        long chunksNumber = daysNumber / daysN;
        var newList = getPairList(daysN, chunksNumber, date, daysNumber);
        pairs.addAll(newList);
        long last = daysNumber % daysN;
        if (last > 0) {
          long daysNum =
              1 + DAYS.between(newList.get(newList.size() - 1).getRight().plusDays(1), endDate);
          var dateN = newList.get(newList.size() - 1).getRight().plusDays(1);
          if (daysNum <= daysN) {
            pairs.add(Pair.of(dateN, endDate));
          }
        }
      }
    }
  }
}