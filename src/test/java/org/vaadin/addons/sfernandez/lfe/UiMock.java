package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.component.*;
import org.mockito.Mockito;

import java.util.Optional;

@Tag("div")
final class UiMock extends Component {

    //---- Attributes ----
    private final UI mockedUi = Mockito.mock(UI.class);

    //---- Constructor ----
    public UiMock() {
//            Mockito.when(mockedUi.access(any())).then(invocation -> {
//                ((Runnable) invocation.getArgument(0)).run();
//                return null;
//            });
    }

    //---- Methods ----
    void attach() {
        super.fireEvent(new AttachEvent(mockedUi, true));
    }

    void detach() {
        super.fireEvent(new DetachEvent(mockedUi));
    }

    @Override
    public Optional<UI> getUI() {
        return Optional.of(mockedUi);
    }

}
