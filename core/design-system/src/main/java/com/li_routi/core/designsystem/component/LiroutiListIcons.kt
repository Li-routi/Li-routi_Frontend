package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp



private const val CameraPath =
    "M11.25 0C11.3529 0.000532574 11.4544 0.02613 11.5449 0.0751953C11.6354 0.124242 11.712 0.195333 " +
        "11.7686 0.28125L12.8379 1.875H16.875C17.0408 1.875 17.2002 1.94041 17.3174 2.05762C17.4346 2.17483 " +
        "17.5 2.33424 17.5 2.5V13.125C17.5 13.2908 17.4346 13.4502 17.3174 13.5674C17.2002 13.6846 17.0408 " +
        "13.75 16.875 13.75H0.625C0.45924 13.75 0.299827 13.6846 0.182617 13.5674C0.0654071 13.4502 0 13.2908 " +
        "0 13.125V2.5C0 2.33424 0.065407 2.17483 0.182617 2.05762C0.299827 1.94041 0.45924 1.875 0.625 1.875H4.66211" +
        "L5.73145 0.28125C5.78805 0.195333 5.86464 0.124242 5.95508 0.0751953C6.04558 0.02613 6.14706 0.000532574 " +
        "6.25 0H11.25ZM5.51855 2.84375C5.46195 2.92967 5.38536 3.00076 5.29492 3.0498C5.20442 3.09887 5.10294 " +
        "3.12447 5 3.125H1.25V12.5H16.25V3.125H12.5C12.3971 3.12447 12.2956 3.09887 12.2051 3.0498C12.1146 " +
        "3.00076 12.038 2.92967 11.9814 2.84375L10.9121 1.25H6.58789L5.51855 2.84375ZM8.01855 3.82227C8.74595 " +
        "3.6776 9.50035 3.75134 10.1855 4.03516C10.8706 4.319 11.4562 4.8004 11.8682 5.41699C12.2801 6.0336 12.5 " +
        "6.75845 12.5 7.5C12.5 8.49456 12.1046 9.44811 11.4014 10.1514C10.6981 10.8546 9.74456 11.25 8.75 " +
        "11.25C8.00845 11.25 7.2836 11.0301 6.66699 10.6182C6.0504 10.2062 5.569 9.62064 5.28516 8.93555C5.00134 " +
        "8.25035 4.9276 7.49595 5.07227 6.76855C5.21696 6.04113 5.57419 5.37308 6.09863 4.84863C6.62308 4.32419 " +
        "7.29113 3.96696 8.01855 3.82227ZM8.75 5C8.2557 5 7.77237 5.14636 7.36133 5.4209C6.9503 5.69554 6.62967 " +
        "6.08629 6.44043 6.54297C6.25127 6.99965 6.20151 7.50249 6.29785 7.9873C6.39431 8.47226 6.63279 8.91795 " +
        "6.98242 9.26758C7.33205 9.61721 7.77774 9.85569 8.2627 9.95215C8.74751 10.0485 9.25035 9.99873 9.70703 " +
        "9.80957C10.1637 9.62033 10.5545 9.2997 10.8291 8.88867C11.1036 8.47763 11.25 7.9943 11.25 7.5C11.25 " +
        "6.83696 10.9864 6.20126 10.5176 5.73242C10.0487 5.26358 9.41304 5 8.75 5Z"

private const val GroupPath =
    "M18.75 15C19.7446 15 20.6981 15.3954 21.4014 16.0986C22.1046 16.8019 22.5 17.7554 22.5 18.75V21H21V18.75" +
        "C21 18.1533 20.7628 17.5811 20.3408 17.1592C19.9189 16.7372 19.3467 16.5 18.75 16.5H15.75C15.1533 16.5 " +
        "14.5811 16.7372 14.1592 17.1592C13.7372 17.5811 13.5 18.1533 13.5 18.75V21H12V18.75C12 17.7554 12.3954 " +
        "16.8019 13.0986 16.0986C13.8019 15.3954 14.7554 15 15.75 15H18.75ZM6.75 9C7.74456 9 8.69811 9.39537 " +
        "9.40137 10.0986C10.1046 10.8019 10.5 11.7554 10.5 12.75V15H9V12.75C9 12.1533 8.76278 11.5811 8.34082 " +
        "11.1592C7.91886 10.7372 7.34674 10.5 6.75 10.5H3.75C3.15326 10.5 2.58114 10.7372 2.15918 11.1592C1.73722 " +
        "11.5811 1.5 12.1533 1.5 12.75V15H0V12.75C0 11.7554 0.395371 10.8019 1.09863 10.0986C1.80189 9.39537 " +
        "2.75544 9 3.75 9H6.75ZM17.25 6C18.2446 6 19.1981 6.39537 19.9014 7.09863C20.6046 7.80189 21 8.75544 21 " +
        "9.75C21 10.4915 20.7801 11.2164 20.3682 11.833C19.9562 12.4496 19.3706 12.931 18.6855 13.2148C18.0004 " +
        "13.4987 17.246 13.5724 16.5186 13.4277C15.7911 13.283 15.1231 12.9258 14.5986 12.4014C14.0742 11.8769 " +
        "13.717 11.2089 13.5723 10.4814C13.4276 9.75405 13.5013 8.99965 13.7852 8.31445C14.069 7.62936 14.5504 " +
        "7.04383 15.167 6.63184C15.7836 6.21992 16.5085 6 17.25 6ZM17.25 7.5C16.6533 7.5 16.0811 7.73722 15.6592 " +
        "8.15918C15.2372 8.58114 15 9.15326 15 9.75C15 10.1949 15.1317 10.63 15.3789 11C15.6261 11.37 15.9776 " +
        "11.6588 16.3887 11.8291C16.7996 11.9993 17.2522 12.0437 17.6885 11.957C18.1249 11.8702 18.5262 11.6555 " +
        "18.8408 11.3408C19.1555 11.0262 19.3702 10.6249 19.457 10.1885C19.5437 9.75218 19.4993 9.29965 19.3291 " +
        "8.88867C19.1588 8.47759 18.87 8.12612 18.5 7.87891C18.13 7.63174 17.6949 7.5 17.25 7.5ZM5.25 0C6.24456 " +
        "2.84136e-07 7.19811 0.395372 7.90137 1.09863C8.60463 1.80189 9 2.75544 9 3.75C9 4.49155 8.78008 5.2164 " +
        "8.36816 5.83301C7.95617 6.4496 7.37064 6.931 6.68555 7.21484C6.00035 7.49866 5.24595 7.5724 4.51855 " +
        "7.42773C3.79113 7.28304 3.12308 6.92581 2.59863 6.40137C2.07419 5.87692 1.71696 5.20887 1.57227 " +
        "4.48145C1.4276 3.75405 1.50134 2.99965 1.78516 2.31445C2.069 1.62936 2.5504 1.04383 3.16699 0.631836" +
        "C3.7836 0.219925 4.50845 0 5.25 0ZM5.25 1.5C4.65326 1.5 4.08114 1.73722 3.65918 2.15918C3.23722 2.58114 " +
        "3 3.15326 3 3.75C3 4.19495 3.13174 4.63002 3.37891 5C3.62612 5.36998 3.97759 5.6588 4.38867 5.8291C4.79965 " +
        "5.99933 5.25218 6.04371 5.68848 5.95703C6.12493 5.87021 6.52615 5.65549 6.84082 5.34082C7.15549 5.02615 " +
        "7.37021 4.62493 7.45703 4.18848C7.54371 3.75218 7.49933 3.29965 7.3291 2.88867C7.1588 2.47759 6.86998 " +
        "2.12612 6.5 1.87891C6.13002 1.63174 5.69495 1.5 5.25 1.5Z"

private const val RoutineBodyPath =
    "M16.8689 24.5H6.94918C5.33778 24.5 3.95948 23.3422 3.68088 21.7546L0.010776 0.826C-0.064824 0.3948 " +
        "0.266976 0 0.704476 0H22.898C23.3355 0 23.6673 0.3948 23.5917 0.826L20.1365 21.7546C19.8579 23.3422 " +
        "18.4803 24.5 16.8689 24.5Z"
private val RoutineBodyColor = Color(0xFFC8E1FF)

private const val RoutineLabelPath =
    "M8.6352 1.0485C5.2899 2.4254 2.6684 2.3764 0 1.5609L2.7307 17.1324C2.8217 17.656 3.2746 18.0361 " +
        "3.8066 18.0361H13.7256C14.2576 18.0361 14.7105 17.6567 14.798 17.1548L17.5364 0.5704C14.6636 " +
        "-0.2745 11.837 -0.2332 8.6352 1.0485Z"

private const val DiamondTopPath1 = "M10.473 4.5136H14.7998L12.2518 0H12.2514L10.473 4.5136Z"
private const val DiamondTopPath2 = "M2.548 0L0 4.5136H4.3268L2.548 0Z"
private const val DiamondTopPath3 = "M10.4734 4.51357H4.32695L7.40015 12.3136L10.4734 4.51357Z"
private val DiamondTopColor = Color(0xFF7D9DFF)

private const val DiamondMidPath = "M4.32656 4.5137H10.473L7.39976 9.76563e-05L4.32656 4.5137Z"
private val DiamondMidColor = Color(0xFF5A81FF)

private const val DiamondLightPath1 = "M0 4.51357L7.4 12.3136L4.3268 4.51357H0Z"
private const val DiamondLightPath2 = "M10.4732 4.51357L7.4 12.3136L14.8 4.51357H10.4732Z"
private const val DiamondLightPath3 = "M7.40005 0H2.54805L4.32685 4.5136L7.40005 0Z"
private const val DiamondLightPath4 = "M10.4732 4.5136L12.2516 0H7.4L10.4732 4.5136Z"
private val DiamondLightColor = Color(0xFFA8BFFF)

@Composable
internal fun LiroutiCameraIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    val image = remember(color) {
        ImageVector.Builder(
            name = "Camera",
            defaultWidth = 17.5.dp,
            defaultHeight = 13.75.dp,
            viewportWidth = 17.5f,
            viewportHeight = 13.75f,
        ).apply {
            addPath(pathData = addPathNodes(CameraPath), fill = SolidColor(color))
        }.build()
    }
    Image(painter = rememberVectorPainter(image), contentDescription = null, modifier = modifier)
}

@Composable
internal fun LiroutiGroupIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    val image = remember(color) {
        ImageVector.Builder(
            name = "Group",
            defaultWidth = 22.5.dp,
            defaultHeight = 21.dp,
            viewportWidth = 22.5f,
            viewportHeight = 21f,
        ).apply {
            addPath(pathData = addPathNodes(GroupPath), fill = SolidColor(color))
        }.build()
    }
    Image(painter = rememberVectorPainter(image), contentDescription = null, modifier = modifier)
}

@Composable
internal fun LiroutiRoutineIcon(modifier: Modifier = Modifier) {
    val bodyImage = remember {
        ImageVector.Builder(
            name = "RoutineBody",
            defaultWidth = 23.6.dp,
            defaultHeight = 24.5.dp,
            viewportWidth = 23.6025f,
            viewportHeight = 24.5f,
        ).apply {
            addPath(pathData = addPathNodes(RoutineBodyPath), fill = SolidColor(RoutineBodyColor))
        }.build()
    }
    val labelImage = remember {
        ImageVector.Builder(
            name = "RoutineLabel",
            defaultWidth = 17.5.dp,
            defaultHeight = 18.dp,
            viewportWidth = 17.5364f,
            viewportHeight = 18.0361f,
        ).apply {
            addPath(pathData = addPathNodes(RoutineLabelPath), fill = SolidColor(Color.White))
        }.build()
    }

    Box(modifier = modifier.size(28.dp)) {
        Image(
            painter = rememberVectorPainter(bodyImage),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 28.dp * 0.0785f, y = 28.dp * 0.0625f)
                .size(23.6.dp, 24.5.dp),
        )
        Image(
            painter = rememberVectorPainter(labelImage),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 28.dp * 0.1908f, y = 28.dp * 0.2139f)
                .size(17.5.dp, 18.dp),
        )
    }
}

@Composable
internal fun LiroutiDiamondIcon(modifier: Modifier = Modifier) {
    val image = remember {
        ImageVector.Builder(
            name = "Diamond",
            defaultWidth = 14.8.dp,
            defaultHeight = 12.32.dp,
            viewportWidth = 14.8001f,
            viewportHeight = 12.3136f,
        ).apply {
            addPath(pathData = addPathNodes(DiamondTopPath1), fill = SolidColor(DiamondTopColor))
            addPath(pathData = addPathNodes(DiamondTopPath2), fill = SolidColor(DiamondTopColor))
            addPath(pathData = addPathNodes(DiamondTopPath3), fill = SolidColor(DiamondTopColor))
            addPath(pathData = addPathNodes(DiamondMidPath), fill = SolidColor(DiamondMidColor))
            addPath(pathData = addPathNodes(DiamondLightPath1), fill = SolidColor(DiamondLightColor))
            addPath(pathData = addPathNodes(DiamondLightPath2), fill = SolidColor(DiamondLightColor))
            addPath(pathData = addPathNodes(DiamondLightPath3), fill = SolidColor(DiamondLightColor))
            addPath(pathData = addPathNodes(DiamondLightPath4), fill = SolidColor(DiamondLightColor))
        }.build()
    }
    Image(painter = rememberVectorPainter(image), contentDescription = null, modifier = modifier)
}
