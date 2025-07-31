package pl.tinks.budgetbuddy.bankholiday

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.ui.theme.StatusBarBackground

@Composable
fun BankHolidayTabs(
    viewModel: BankHolidayViewModel = hiltViewModel(),
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val regions = listOf(
        stringResource(R.string.region_england_and_wales),
        stringResource(R.string.region_scotland),
        stringResource(R.string.region_northern_ireland)
    )
    val pagerState = rememberPagerState { regions.size }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        StatusBarBackground(color = MaterialTheme.colorScheme.surface)
        TabRow(selectedTabIndex = pagerState.currentPage) {
            regions.forEachIndexed { idx, region ->
                Tab(selected = pagerState.currentPage == idx, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(idx)
                    }
                }, text = { Text(region) })
            }
        }
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize()
        ) { page ->
            BankHolidayScreen(
                viewModel = viewModel,
                region = regions[page],
                onErrorDialogDismiss = onErrorDialogDismiss
            )
        }
    }
}
