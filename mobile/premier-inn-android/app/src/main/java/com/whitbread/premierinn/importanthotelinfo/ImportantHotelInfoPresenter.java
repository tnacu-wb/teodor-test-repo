package com.whitbread.premierinn.importanthotelinfo;

import androidx.annotation.NonNull;
import com.whitbread.premierinn.api.response.booking.BookingNote;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@ActivityRetainedScoped
public class ImportantHotelInfoPresenter extends Presenter<ImportantHotelInfoPresenter.View> {

    private List<BookingNote> bookingNotes;
    private List<InfoItem> infoItems;
    private final CompositeDisposable viewCompositeDisposable;
    private LocalDate arrivalDate;
    private LocalDate departureDate;

    @Inject
    public ImportantHotelInfoPresenter(@NonNull CompositeDisposable compositeDisposable) {
        this.viewCompositeDisposable = compositeDisposable;
    }

    public void initParams(
            @NonNull List<BookingNote> bookingNotes,
            @NonNull List<InfoItem> infoItems,
            LocalDate arrivalDate,
            LocalDate departureDate
    ) {
        this.bookingNotes = bookingNotes;
        this.infoItems = infoItems;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    @Override
    protected void onAttachView(View view) {
        if (!bookingNotes.isEmpty()) {
            bookingNotes.sort(Comparator.comparingInt(BookingNote::priority));
            view.showBookingNotes(bookingNotes);
        }
        if (!infoItems.isEmpty()) {
            boolean isPriorityEmpty = infoItems.stream()
                    .map(InfoItem::getPriority)
                    .anyMatch(String::isEmpty);
            if (!isPriorityEmpty) {
                infoItems.sort(Comparator.comparingInt(infoItem -> Integer.parseInt(infoItem.getPriority())));
            }

            DateTimeFormatter df = DateTimeFormatter.ofPattern(DateFormat.SLASHED_DAY_MONTH_YEAR);
            List<InfoItem> filteredInfoItems = infoItems.stream()
                    .filter(infoItem -> {
                        if (infoItem.getStartDate().isEmpty() || infoItem.getEndDate().isEmpty()) {
                            return false;
                        }
                        LocalDate startDate = LocalDate.parse(infoItem.getStartDate(), df);
                        LocalDate endDate = LocalDate.parse(infoItem.getEndDate(), df);
                        return !arrivalDate.isAfter(endDate) && !departureDate.isBefore(startDate);
                    }).collect(Collectors.toList());
            view.showImportantInfo(filteredInfoItems);
        }
        viewCompositeDisposable.add(view.onCrossClick().subscribe(__ -> view.closeScreen()));
    }

    @Override
    protected void onDetachView() {
        if (viewCompositeDisposable.size() > 0) {
            viewCompositeDisposable.clear();
        }
    }

    public interface View extends PresenterView {

        void showBookingNotes(List<BookingNote> bookingNotes);

        void showImportantInfo(List<InfoItem> infoItems);

        void closeScreen();

        Observable<Unit> onCrossClick();
    }
}
